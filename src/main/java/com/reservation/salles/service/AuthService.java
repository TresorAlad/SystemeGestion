package com.reservation.salles.service;

import com.reservation.salles.dao.UtilisateurDAO;
import com.reservation.salles.model.Utilisateur;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur login(String email, String motDePasse) {
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            return null;
        }
        return utilisateurDAO.findByEmailAndPassword(email.trim(), motDePasse.trim());
    }
}

