package com.reservation.salles.dao;

import com.reservation.salles.model.Equipement;
import com.reservation.salles.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementDAO {

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

    public void deleteAll() {
        String sql = "DELETE FROM equipements";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Equipement map(ResultSet rs) throws SQLException {
        Equipement e = new Equipement();
        e.setIdEquipement(rs.getInt("id_equipement"));
        e.setNom(rs.getString("nom"));
        e.setQuantite(rs.getInt("quantite"));
        return e;
    }
}

