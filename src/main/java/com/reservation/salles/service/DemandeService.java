package com.reservation.salles.service;

import com.reservation.salles.dao.DemandeDAO;
import com.reservation.salles.dao.ReservationDAO;
import com.reservation.salles.model.Demande;
import com.reservation.salles.model.Reservation;
import com.reservation.salles.util.NotificationUtil;

import java.util.List;

/**
 * Service gérant le cycle de vie des demandes d'approbation.
 * Utilisé par les gestionnaires pour valider ou rejeter les réservations des
 * utilisateurs.
 */
public class DemandeService {

    private final DemandeDAO demandeDAO = new DemandeDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    /**
     * Liste toutes les demandes en attente de décision.
     */
    public List<Demande> listerDemandesEnAttente() {
        return demandeDAO.findAllEnAttente();
    }

    /**
     * Valide une demande : met à jour le statut de la demande et de la réservation
     * associée à 'VALIDEE'.
     */
    public void validerDemande(Demande demande) {
        Reservation r = demande.getReservation();

        demande.setStatut("VALIDEE");
        demandeDAO.updateStatut(demande.getIdDemande(), "VALIDEE");

        r.setStatut("VALIDEE");
        reservationDAO.updateStatut(r.getIdReservation(), "VALIDEE");

        NotificationUtil.succes("Réservation validée pour la salle " +
                r.getSalle().getNom() + " le " + r.getDate());
    }

    /**
     * Rejette une demande : met à jour le statut de la demande et de la réservation
     * associée à 'REJETEE'.
     */
    public void rejeterDemande(Demande demande) {
        Reservation r = demande.getReservation();

        demande.setStatut("REJETEE");
        demandeDAO.updateStatut(demande.getIdDemande(), "REJETEE");

        r.setStatut("REJETEE");
        reservationDAO.updateStatut(r.getIdReservation(), "REJETEE");

        NotificationUtil.info("La réservation a été rejetée pour la salle " +
                r.getSalle().getNom() + " le " + r.getDate());
    }
}
