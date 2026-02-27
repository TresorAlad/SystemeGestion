package com.reservation.salles.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gère la connexion à la base de données SQLite.
 * Utilise le pattern Singleton implicite via une méthode statique pour obtenir
 * la connexion.
 */
public class DBConnection {

    private static final String URL = "jdbc:sqlite:reservation.db";

    static {
        try {
            // Chargement explicite du driver SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Impossible de charger le driver SQLite", e);
        }
    }

    /**
     * Établit et retourne une nouvelle connexion vers la base de données.
     * 
     * @return Connection active vers reservation.db
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
