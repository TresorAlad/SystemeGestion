package com.reservation.salles.dao;

import com.reservation.salles.model.Demande;
import com.reservation.salles.model.Reservation;
import com.reservation.salles.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des demandes (création, modification, suppression de
 * réservation).
 */
public class DemandeDAO {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    /**
     * Enregistre une nouvelle demande dans la base de données.
     */
    public Demande save(Demande d) {
        String sql = "INSERT INTO demandes (id_reservation, type_demande, date_demande, statut) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, d.getReservation().getIdReservation());
            ps.setString(2, d.getTypeDemande());
            ps.setString(3, d.getDateDemande().toString());
            ps.setString(4, d.getStatut());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setIdDemande(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * Met à jour le statut d'une demande (VALIDEE, REJETEE).
     */
    public void updateStatut(int idDemande, String statut) {
        String sql = "UPDATE demandes SET statut = ? WHERE id_demande = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setInt(2, idDemande);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère toutes les demandes qui sont encore en attente de traitement.
     */
    public List<Demande> findAllEnAttente() {
        List<Demande> result = new ArrayList<>();
        String sql = "SELECT * FROM demandes WHERE statut = 'EN_ATTENTE' ORDER BY date_demande";
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
     * Recherche une demande par son identifiant.
     */
    public Demande findById(int idDemande) {
        String sql = "SELECT * FROM demandes WHERE id_demande = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDemande);
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
     * Mappe une ligne de résultat SQL vers un objet Demande.
     */
    private Demande map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_demande");
        int idReservation = rs.getInt("id_reservation");
        String type = rs.getString("type_demande");
        LocalDate dateDemande = LocalDate.parse(rs.getString("date_demande"));
        String statut = rs.getString("statut");

        Reservation reservation = reservationDAO.findById(idReservation);
        return new Demande(id, reservation, type, dateDemande, statut);
    }
}
