package com.reservation.salles.dao;

import com.reservation.salles.model.Salle;
import com.reservation.salles.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> findAll() {
        List<Salle> result = new ArrayList<>();
        String sql = "SELECT * FROM salles ORDER BY nom";
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

    public Salle findById(int id) {
        String sql = "SELECT * FROM salles WHERE id_salle = ?";
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

    public Salle save(Salle s) {
        String sql = "INSERT INTO salles (nom, type, capacite, disponible, photo) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNom());
            ps.setString(2, s.getType());
            ps.setInt(3, s.getCapacite());
            ps.setInt(4, s.isDisponible() ? 1 : 0);
            ps.setString(5, s.getPhoto());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setIdSalle(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }

    public int countDisponibles() {
        String sql = "SELECT COUNT(*) FROM salles WHERE disponible = 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean update(Salle s) {
        String sql = "UPDATE salles SET nom=?, type=?, capacite=?, disponible=?, photo=? WHERE id_salle=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getNom());
            ps.setString(2, s.getType());
            ps.setInt(3, s.getCapacite());
            ps.setInt(4, s.isDisponible() ? 1 : 0);
            ps.setString(5, s.getPhoto());
            ps.setInt(6, s.getIdSalle());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM salles WHERE id_salle = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Salle map(ResultSet rs) throws SQLException {
        Salle s = new Salle();
        s.setIdSalle(rs.getInt("id_salle"));
        s.setNom(rs.getString("nom"));
        s.setType(rs.getString("type"));
        s.setCapacite(rs.getInt("capacite"));
        s.setDisponible(rs.getInt("disponible") == 1);
        try {
            s.setPhoto(rs.getString("photo"));
        } catch (SQLException ignored) {
        }
        return s;
    }
}
