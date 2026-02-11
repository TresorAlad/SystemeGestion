package com.reservation.salles.service;

import com.reservation.salles.dao.DemandeDAO;
import com.reservation.salles.dao.ReservationDAO;
import com.reservation.salles.model.Demande;
import com.reservation.salles.model.Reservation;
import com.reservation.salles.util.NotificationUtil;

import java.util.List;

public class DemandeService {

    private final DemandeDAO demandeDAO = new DemandeDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    public List<Demande> listerDemandesEnAttente() {
        return demandeDAO.findAllEnAttente();
    }

    public void validerDemande(Demande demande) {
        Reservation r = demande.getReservation();

        demande.setStatut("VALIDEE");
        demandeDAO.updateStatut(demande.getIdDemande(), "VALIDEE");

        r.setStatut("VALIDEE");
        reservationDAO.updateStatut(r.getIdReservation(), "VALIDEE");

        NotificationUtil.succes("Réservation validée pour la salle " +
                r.getSalle().getNom() + " le " + r.getDate());
    }

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

