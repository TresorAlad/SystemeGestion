package com.reservation.salles.service;

import com.reservation.salles.dao.DemandeDAO;
import com.reservation.salles.dao.ReservationDAO;
import com.reservation.salles.model.Demande;
import com.reservation.salles.model.Reservation;
import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service gérant la logique métier des réservations.
 * Coordonne les actions entre ReservationDAO et DemandeDAO.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DemandeDAO demandeDAO = new DemandeDAO();

    /**
     * Crée une nouvelle réservation.
     * Si l'utilisateur est un gestionnaire, elle est validée automatiquement.
     * Sinon, elle passe par une demande en attente.
     * 
     * @return La réservation créée ou null si la salle n'est pas disponible.
     */
    public Reservation creerReservation(Utilisateur utilisateur, Salle salle,
            LocalDate date, LocalTime debut, LocalTime fin, String nom, String telephone, String objet) {
        if (!reservationDAO.isSalleDisponible(salle.getIdSalle(), date, debut, fin)) {
            return null;
        }

        boolean isManager = utilisateur.estGestionnaire();
        String statut = isManager ? "VALIDEE" : "EN_ATTENTE";

        Reservation reservation = new Reservation(0, utilisateur, salle, date, debut, fin, statut, nom, telephone,
                objet);
        reservationDAO.save(reservation);

        // Si c'est un utilisateur standard, on crée une demande d'approbation
        if (!isManager) {
            Demande demande = new Demande();
            demande.setReservation(reservation);
            demande.setTypeDemande("CREATION");
            demande.setDateDemande(LocalDate.now());
            demande.setStatut("EN_ATTENTE");
            demandeDAO.save(demande);
        }

        return reservation;
    }

    /**
     * Annule une réservation par son détenteur ou un admin.
     */
    public void annulerReservation(Reservation reservation) {
        reservation.setStatut("ANNULEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "ANNULEE");
    }

    /**
     * Valide une réservation (Action Gestionnaire).
     */
    public void validerReservation(Reservation reservation) {
        reservation.setStatut("VALIDEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "VALIDEE");
    }

    /**
     * Rejette une réservation (Action Gestionnaire).
     */
    public void rejeterReservation(Reservation reservation) {
        reservation.setStatut("REJETEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "REJETEE");
    }

    /**
     * Liste toutes les réservations du système.
     */
    public List<Reservation> listerToutesLesReservations() {
        return reservationDAO.findAll();
    }

    /**
     * Liste les réservations d'un utilisateur donné.
     */
    public List<Reservation> listerParUtilisateur(int idUtilisateur) {
        return reservationDAO.findByUtilisateur(idUtilisateur);
    }

    /**
     * Liste les réservations les plus récentes.
     */
    public List<Reservation> listerRecentes(int limit) {
        return reservationDAO.findRecent(limit);
    }

    /**
     * Retourne le nombre total de réservations validées.
     */
    public int compterReservationsActives() {
        return reservationDAO.countByStatut("VALIDEE");
    }
}
