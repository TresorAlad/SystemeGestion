package com.reservation.salles.model;

import java.time.LocalDate;

public class Demande {

    private int idDemande;
    private Reservation reservation;
    private String typeDemande;
    private LocalDate dateDemande;
    private String statut; // EN_ATTENTE, VALIDEE, REJETEE

    public Demande() {
    }

    public Demande(int idDemande, Reservation reservation, String typeDemande,
                   LocalDate dateDemande, String statut) {
        this.idDemande = idDemande;
        this.reservation = reservation;
        this.typeDemande = typeDemande;
        this.dateDemande = dateDemande;
        this.statut = statut;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(String typeDemande) {
        this.typeDemande = typeDemande;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void mettreAJour(String nouveauStatut) {
        this.statut = nouveauStatut;
    }

    public void soumettre() {
        this.statut = "EN_ATTENTE";
    }
}

