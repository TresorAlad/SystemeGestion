package com.reservation.salles.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NotificationUtil {

    public static void showPage(javafx.scene.Node node, String title, String description,
            com.reservation.salles.model.Utilisateur user, boolean success) {
        if (node == null || node.getScene() == null || node.getScene().getWindow() == null) {
            System.out.println((success ? "SUCCESS: " : "ERROR: ") + title + " - " + description);
            return;
        }
        Stage stage = (Stage) node.getScene().getWindow();
        if (success) {
            showSuccess(stage, title, description, user);
        } else {
            showError(stage, title, description, user);
        }
    }

    public static void showSuccess(Stage stage, String title, String description,
            com.reservation.salles.model.Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(NotificationUtil.class.getResource("/fxml/notification-view.fxml"));
            Parent root = loader.load();

            com.reservation.salles.ui.controller.NotificationController controller = loader.getController();
            controller.initSuccess(title, description, user);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(NotificationUtil.class.getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("SUCCESS (fallback): " + title + ": " + description);
        }
    }

    public static void showError(Stage stage, String title, String description,
            com.reservation.salles.model.Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(NotificationUtil.class.getResource("/fxml/notification-view.fxml"));
            Parent root = loader.load();

            com.reservation.salles.ui.controller.NotificationController controller = loader.getController();
            controller.initError(title, description, user);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(NotificationUtil.class.getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR (fallback): " + title + ": " + description);
        }
    }

    public static void info(String message) {
        System.out.println("INFO: " + message);
    }

    public static void succes(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public static void erreur(String message) {
        System.err.println("ERROR: " + message);
    }
}
