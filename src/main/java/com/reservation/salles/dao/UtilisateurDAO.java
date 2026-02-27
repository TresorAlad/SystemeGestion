package com.reservation.salles.dao;

import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO pour la gestion des utilisateurs (utilisateurs standards et
 * gestionnaires).
 */
public class UtilisateurDAO {

    /**
     * Recherche un utilisateur par son adresse email.
     */
    public Utilisateur findByEmail(String email) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Valide les identifiants de connexion (email et mot de passe).
     */
    public Utilisateur findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND mot_de_passe = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recherche un utilisateur par son ID unique.
     */
    public Utilisateur findById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id_utilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Enregistre un nouvel utilisateur (Inscription).
     */
    public Utilisateur save(Utilisateur u) {
        String sql = "INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getMotDePasse());
            ps.setString(4, u.getRole());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setIdUtilisateur(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    /**
     * Liste tous les utilisateurs inscrits.
     */
    public java.util.List<Utilisateur> findAll() {
        java.util.List<Utilisateur> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY nom";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Met à jour le rôle d'un utilisateur ( प्रमोशन GESTIONNAIRE ).
     */
    public void updateRole(int id, String role) {
        String sql = "UPDATE utilisateurs SET role = ? WHERE id_utilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour les informations de profil (Nom et Mot de passe).
     */
    public void updateProfile(int id, String nom, String motDePasse) {
        String sql = "UPDATE utilisateurs SET nom = ?, mot_de_passe = ? WHERE id_utilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, motDePasse);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mappe une ligne de ResultSet vers un objet Utilisateur.
     */
    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(rs.getInt("id_utilisateur"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
