package com.reservation.salles.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {

    private int idReservation;
    private Utilisateur utilisateur;
    private Salle salle;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String statut;
    private String nomReservataire;
    private String telephone;
    private String objet;

    public Reservation() {
    }

    public Reservation(int idReservation,
            Utilisateur utilisateur,
            Salle salle,
            LocalDate date,
            LocalTime heureDebut,
            LocalTime heureFin,
            String statut,
            String nomReservataire,
            String telephone,
            String objet) {
        this.idReservation = idReservation;
        this.utilisateur = utilisateur;
        this.salle = salle;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.statut = statut;
        this.nomReservataire = nomReservataire;
        this.telephone = telephone;
        this.objet = objet;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNomReservataire() {
        return nomReservataire;
    }

    public void setNomReservataire(String nomReservataire) {
        this.nomReservataire = nomReservataire;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public void annuler() {
        this.statut = "ANNULEE";
    }
}
