package com.reservation.salles.model;

public class Equipement {

    private int idEquipement;
    private String nom;
    private int quantite;

    public Equipement() {
    }

    public Equipement(int idEquipement, String nom, int quantite) {
        this.idEquipement = idEquipement;
        this.nom = nom;
        this.quantite = quantite;
    }

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

