package com.reservation.salles.model;

public class Salle {

    private int idSalle;
    private String nom;
    private String type;
    private int capacite;
    private boolean disponible;
    private String photo;

    public Salle() {
    }

    public Salle(int idSalle, String nom, String type, int capacite, boolean disponible) {
        this.idSalle = idSalle;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.disponible = disponible;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return nom + " (" + capacite + " places)";
    }
}

