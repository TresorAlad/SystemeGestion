package com.reservation.salles.model;

/**
 * Représente un équipement (ex: Projecteur, Tableau, etc.) associé à une salle.
 */
public class Equipement {

    private int idEquipement;
    private String nom;
    private int quantite;

    public Equipement() {
    }

    /**
     * Constructeur pour un équipement avec son nom et sa quantité initiale.
     */
    public Equipement(int idEquipement, String nom, int quantite) {
        this.idEquipement = idEquipement;
        this.nom = nom;
        this.quantite = quantite;
    }

    // Getters et Setters standard
    public int getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(int idEquipement) {
        this.idEquipement = idEquipement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return nom + " (" + quantite + ")";
    }
}
