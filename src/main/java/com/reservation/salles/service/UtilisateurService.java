package com.reservation.salles.service;

import com.reservation.salles.dao.UtilisateurDAO;
import com.reservation.salles.model.Utilisateur;
import java.util.List;

/**
 * Service pour la gestion des comptes utilisateurs.
 */
public class UtilisateurService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Liste tous les utilisateurs inscrits sur la plateforme.
     */
    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurDAO.findAll();
    }

    /**
     * Modifie le rôle d'un utilisateur pour lui donner les droits de gestionnaire.
     */
    public void promouvoirEnGestionnaire(int idUtilisateur) {
        utilisateurDAO.updateRole(idUtilisateur, "GESTIONNAIRE");
    }

    /**
     * Met à jour les informations personnelles (Nom, Password) d'un utilisateur.
     */
    public void mettreAJourProfil(int idUtilisateur, String nom, String motDePasse) {
        utilisateurDAO.updateProfile(idUtilisateur, nom, motDePasse);
    }
}
