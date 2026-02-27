package com.reservation.salles.dao;

import com.reservation.salles.model.Equipement;
import com.reservation.salles.model.Salle;
import com.reservation.salles.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des salles.
 * Gère les opérations CRUD sur la table 'salles' et la liaison avec les
 * équipements.
 */
public class SalleDAO {

    private final EquipementDAO equipementDAO = new EquipementDAO();

    /**
     * Récupère toutes les salles avec leurs équipements associés.
     */
    public List<Salle> findAll() {
        List<Salle> result = new ArrayList<>();
        String sql = "SELECT * FROM salles ORDER BY nom";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Salle s = map(rs);
                s.setEquipements(fetchEquipements(s.getIdSalle()));
                result.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Recherche une salle spécifique par son ID.
     */
    public Salle findById(int id) {
        String sql = "SELECT * FROM salles WHERE id_salle = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Salle s = map(rs);
                    s.setEquipements(fetchEquipements(s.getIdSalle()));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Synchronise les équipements d'une salle dans la table de liaison.
     * Supprime les anciens liens et recrée les nouveaux.
     */
    public void saveEquipements(int idSalle, List<Equipement> equipements) {
        String deleteSql = "DELETE FROM salle_equipements WHERE id_salle = ?";
        String insertSql = "INSERT INTO salle_equipements (id_salle, id_equipement, quantite) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Nettoyage avant insertion
                try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                    deletePs.setInt(1, idSalle);
                    deletePs.executeUpdate();
                }

                // Insertion des équipements (création si nouveau nom)
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    for (Equipement eq : equipements) {
                        Equipement existing = equipementDAO.findByName(eq.getNom());
                        if (existing == null) {
                            existing = equipementDAO.save(eq);
                        }

                        insertPs.setInt(1, idSalle);
                        insertPs.setInt(2, existing.getIdEquipement());
                        insertPs.setInt(3, eq.getQuantite());
                        insertPs.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère la liste des équipements d'une salle via une jointure SQL.
     */
    private List<Equipement> fetchEquipements(int idSalle) {
        List<Equipement> list = new ArrayList<>();
        String sql = "SELECT e.id_equipement, e.nom, se.quantite " +
                "FROM equipements e " +
                "JOIN salle_equipements se ON e.id_equipement = se.id_equipement " +
                "WHERE se.id_salle = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSalle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Equipement(
                            rs.getInt("id_equipement"),
                            rs.getString("nom"),
                            rs.getInt("quantite")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Insère une nouvelle salle.
     */
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

    /**
     * Compte le nombre total de salles marquées comme 'disponible'.
     */
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

    /**
     * Met à jour les informations d'une salle.
     */
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

    /**
     * Supprime une salle de la base de données.
     */
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

    /**
     * Mappe une ligne de ResultSet vers un objet Salle.
     */
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
