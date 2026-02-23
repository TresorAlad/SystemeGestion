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

public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DemandeDAO demandeDAO = new DemandeDAO();

    public Reservation creerReservation(Utilisateur utilisateur, Salle salle,
            LocalDate date, LocalTime debut, LocalTime fin) {
        if (!reservationDAO.isSalleDisponible(salle.getIdSalle(), date, debut, fin)) {
            return null;
        }

        boolean isManager = utilisateur.estGestionnaire();
        String statut = isManager ? "VALIDEE" : "EN_ATTENTE";

        Reservation reservation = new Reservation(0, utilisateur, salle, date, debut, fin, statut);
        reservationDAO.save(reservation);

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

    public void annulerReservation(Reservation reservation) {
        reservation.setStatut("ANNULEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "ANNULEE");
    }

    public void validerReservation(Reservation reservation) {
        reservation.setStatut("VALIDEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "VALIDEE");
    }

    public void rejeterReservation(Reservation reservation) {
        reservation.setStatut("REJETEE");
        reservationDAO.updateStatut(reservation.getIdReservation(), "REJETEE");
    }

    public List<Reservation> listerToutesLesReservations() {
        return reservationDAO.findAll();
    }

    public List<Reservation> listerParUtilisateur(int idUtilisateur) {
        return reservationDAO.findByUtilisateur(idUtilisateur);
    }

    public List<Reservation> listerRecentes(int limit) {
        return reservationDAO.findRecent(limit);
    }

    public int compterReservationsActives() {
        return reservationDAO.countByStatut("VALIDEE");
    }
}
