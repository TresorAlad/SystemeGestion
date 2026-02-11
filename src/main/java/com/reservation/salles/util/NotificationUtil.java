package com.reservation.salles.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class NotificationUtil {

    private static void show(AlertType type, String title, String message) {
        Runnable r = () -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        };
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void info(String message) {
        show(AlertType.INFORMATION, "Information", message);
    }

    public static void succes(String message) {
        show(AlertType.INFORMATION, "Succès", message);
    }

    public static void erreur(String message) {
        show(AlertType.ERROR, "Erreur", message);
    }
}

