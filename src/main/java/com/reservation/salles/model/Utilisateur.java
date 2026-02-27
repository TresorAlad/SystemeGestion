package com.reservation.salles.model;

/**
 * Représente un utilisateur du système.
 * Un utilisateur peut être soit un demandeur standard, soit un gestionnaire des
 * salles.
 */
public class Utilisateur {

    private int idUtilisateur;
    private String nom;
    private String email;
    private String motDePasse;
    private String role; // UTILISATEUR ou GESTIONNAIRE

    public Utilisateur() {
    }

    /**
     * Constructeur complet pour un utilisateur.
     */
    public Utilisateur(int idUtilisateur, String nom, String email, String motDePasse, String role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // Getters et Setters standard avec commentaires descriptifs
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

    /**
     * Vérifie si l'utilisateur possède les droits de gestionnaire.
     * 
     * @return true si l'utilisateur est un gestionnaire.
     */
    public boolean estGestionnaire() {
        return "GESTIONNAIRE".equalsIgnoreCase(role);
    }

    /**
     * Vérifie si l'utilisateur est un simple utilisateur standard.
     * 
     * @return true si le rôle est UTILISATEUR.
     */
    public boolean estUtilisateur() {
        return "UTILISATEUR".equalsIgnoreCase(role);
    }
}
