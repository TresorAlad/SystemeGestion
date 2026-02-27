package com.reservation.salles.dao;

import com.reservation.salles.model.Equipement;
import com.reservation.salles.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des types d'équipements disponibles dans le catalogue.
 */
public class EquipementDAO {

    /**
     * Liste tous les équipements disponibles dans le système.
     */
    public List<Equipement> findAll() {
        List<Equipement> result = new ArrayList<>();
        String sql = "SELECT * FROM equipements ORDER BY nom";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Enregistre un nouvel équipement dans le catalogue.
     */
    public Equipement save(Equipement e) {
        String sql = "INSERT INTO equipements (nom, quantite) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getNom());
            ps.setInt(2, e.getQuantite());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setIdEquipement(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return e;
    }

    /**
     * Recherche un équipement par son nom exact.
     */
    public Equipement findByName(String nom) {
        String sql = "SELECT * FROM equipements WHERE nom = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
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
     * Supprime tous les équipements (Utilisé pour réinitialisation).
     */
    public void deleteAll() {
        String sql = "DELETE FROM equipements";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mappe une ligne de ResultSet vers un objet Equipement.
     */
    private Equipement map(ResultSet rs) throws SQLException {
        Equipement e = new Equipement();
        e.setIdEquipement(rs.getInt("id_equipement"));
        e.setNom(rs.getString("nom"));
        e.setQuantite(rs.getInt("quantite"));
        return e;
    }
}
