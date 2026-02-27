package com.reservation.salles.service;

import com.reservation.salles.dao.UtilisateurDAO;
import com.reservation.salles.model.Utilisateur;

/**
 * Service gérant l'authentification des utilisateurs.
 */
public class AuthService {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Tente d'authentifier un utilisateur avec son email et son mot de passe.
     * 
     * @return L'objet Utilisateur si les identifiants sont corrects, sinon null.
     */
    public Utilisateur login(String email, String motDePasse) {
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            return null;
        }
        return utilisateurDAO.findByEmailAndPassword(email.trim(), motDePasse.trim());
    }
}
