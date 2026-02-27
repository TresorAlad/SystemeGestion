package com.reservation.salles.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une salle de réunion ou de conférence.
 */
public class Salle {

    private int idSalle;
    private String nom;
    private String type; // ex: Réunion, Conférence, Labo
    private int capacite;
    private boolean disponible; // Etat général (active ou non)
    private String photo; // Chemin de l'image
    private List<Equipement> equipements = new ArrayList<>();

    public Salle() {
    }

    /**
     * Constructeur rapide pour l'initialisation.
     */
    public Salle(int idSalle, String nom, String type, int capacite, boolean disponible) {
        this.idSalle = idSalle;
        this.nom = nom;
        this.type = type;
        this.capacite = capacite;
        this.disponible = disponible;
    }

    // Getters et Setters
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

    /**
     * Récupère la liste des équipements présents dans la salle.
     */
    public List<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(List<Equipement> equipements) {
        this.equipements = equipements;
    }

    @Override
    public String toString() {
        return nom + " (" + capacite + " places)";
    }
}
