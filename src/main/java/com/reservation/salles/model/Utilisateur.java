package com.reservation.salles.model;

public class Utilisateur {

    private int idUtilisateur;
    private String nom;
    private String email;
    private String motDePasse;
    private String role; // UTILISATEUR ou GESTIONNAIRE

    public Utilisateur() {
    }

    public Utilisateur(int idUtilisateur, String nom, String email, String motDePasse, String role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean estGestionnaire() {
        return "GESTIONNAIRE".equalsIgnoreCase(role);
    }

    public boolean estUtilisateur() {
        return "UTILISATEUR".equalsIgnoreCase(role);
    }
}

