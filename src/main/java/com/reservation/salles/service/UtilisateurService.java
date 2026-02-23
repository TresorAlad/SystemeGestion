package com.reservation.salles.service;

import com.reservation.salles.dao.UtilisateurDAO;
import com.reservation.salles.model.Utilisateur;
import java.util.List;

public class UtilisateurService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public List<Utilisateur> listerTousLesUtilisateurs() {
        return utilisateurDAO.findAll();
    }

    public void promouvoirEnGestionnaire(int idUtilisateur) {
        utilisateurDAO.updateRole(idUtilisateur, "GESTIONNAIRE");
    }

    public void mettreAJourProfil(int idUtilisateur, String nom, String motDePasse) {
        utilisateurDAO.updateProfile(idUtilisateur, nom, motDePasse);
    }
}
