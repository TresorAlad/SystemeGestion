package com.reservation.salles.model;

import java.time.LocalDate;

/**
 * Représente une demande de réservation ou de modification soumise par un
 * utilisateur.
 * Une demande est ensuite validée ou rejetée par un gestionnaire.
 */
public class Demande {

    private int idDemande;
    private Reservation reservation;
    private String typeDemande; // CREATION, MODIFICATION, SUPPRESSION
    private LocalDate dateDemande;
    private String statut; // EN_ATTENTE, VALIDEE, REJETEE

    public Demande() {
    }

    /**
     * Constructeur pour initialiser une nouvelle demande.
     */
    public Demande(int idDemande, Reservation reservation, String typeDemande,
            LocalDate dateDemande, String statut) {
        this.idDemande = idDemande;
        this.reservation = reservation;
        this.typeDemande = typeDemande;
        this.dateDemande = dateDemande;
        this.statut = statut;
    }

    // Getters et Setters standard
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

    /**
     * Met à jour le statut de la demande.
     * 
     * @param nouveauStatut ex: VALIDEE ou REJETEE
     */
    public void mettreAJour(String nouveauStatut) {
        this.statut = nouveauStatut;
    }

    /**
     * Définit le statut de la demande sur "EN_ATTENTE".
     */
    public void soumettre() {
        this.statut = "EN_ATTENTE";
    }
}
