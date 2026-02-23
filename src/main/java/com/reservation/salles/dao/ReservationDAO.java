package com.reservation.salles.dao;

import com.reservation.salles.model.Reservation;
import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final SalleDAO salleDAO = new SalleDAO();

    public Reservation save(Reservation r) {
        String sql = "INSERT INTO reservations (id_utilisateur, id_salle, date, heure_debut, heure_fin, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getUtilisateur().getIdUtilisateur());
            ps.setInt(2, r.getSalle().getIdSalle());
            ps.setString(3, r.getDate().toString());
            ps.setString(4, r.getHeureDebut().toString());
            ps.setString(5, r.getHeureFin().toString());
            ps.setString(6, r.getStatut());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdReservation(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public void updateStatut(int idReservation, String statut) {
        String sql = "UPDATE reservations SET statut = ? WHERE id_reservation = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setInt(2, idReservation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> findAll() {
        List<Reservation> result = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY date DESC, heure_debut DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Reservation> findByUtilisateur(int idUtilisateur) {
        List<Reservation> result = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE id_utilisateur = ? ORDER BY date, heure_debut";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isSalleDisponible(int idSalle, LocalDate date,
            LocalTime heureDebut, LocalTime heureFin) {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE id_salle = ? AND date = ? " +
                "AND statut IN ('EN_ATTENTE','VALIDEE') " +
                "AND ( (heure_debut < ? AND heure_fin > ?) " +
                "   OR (heure_debut >= ? AND heure_debut < ?) " +
                "   OR (heure_fin > ? AND heure_fin <= ?) )";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSalle);
            ps.setString(2, date.toString());
            ps.setString(3, heureFin.toString());
            ps.setString(4, heureDebut.toString());
            ps.setString(5, heureDebut.toString());
            ps.setString(6, heureFin.toString());
            ps.setString(7, heureDebut.toString());
            ps.setString(8, heureFin.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Reservation findById(int idReservation) {
        String sql = "SELECT * FROM reservations WHERE id_reservation = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReservation);
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

    public List<Reservation> findRecent(int limit) {
        List<Reservation> result = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY date DESC, heure_debut DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int countByStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE statut = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        int idReservation = rs.getInt("id_reservation");
        int idUtilisateur = rs.getInt("id_utilisateur");
        int idSalle = rs.getInt("id_salle");
        LocalDate date = LocalDate.parse(rs.getString("date"));
        LocalTime debut = LocalTime.parse(rs.getString("heure_debut"));
        LocalTime fin = LocalTime.parse(rs.getString("heure_fin"));
        String statut = rs.getString("statut");

        Utilisateur u = utilisateurDAO.findById(idUtilisateur);
        Salle s = salleDAO.findById(idSalle);

        return new Reservation(idReservation, u, s, date, debut, fin, statut);
    }
}
