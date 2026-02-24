package com.reservation.salles.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationController {

    @FXML
    private Label iconLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private javafx.scene.control.Button actionButton;

    private com.reservation.salles.model.Utilisateur currentUser;

    public void initSuccess(String message, String description, com.reservation.salles.model.Utilisateur user) {
        this.currentUser = user;
        iconLabel.setText("✓");
        iconLabel.getStyleClass().add("icon-success");
        messageLabel.setText(message);
        descriptionLabel.setText(description);
        actionButton.setText("Continuer");
    }

    public void initError(String message, String description, com.reservation.salles.model.Utilisateur user) {
        this.currentUser = user;
        iconLabel.setText("✕");
        iconLabel.getStyleClass().add("icon-error");
        messageLabel.setText(message);
        descriptionLabel.setText(description);
        actionButton.setText("Réessayer");
    }

    @FXML
    private void handleAction() {
        try {
            String fxml;
            if (currentUser == null) {
                fxml = "/fxml/login-view.fxml";
            } else {
                fxml = currentUser.estGestionnaire()
                        ? "/fxml/manager-dashboard.fxml"
                        : "/fxml/user-dashboard.fxml";
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxml));
            javafx.scene.Parent root = loader.load();

            if (currentUser != null) {
                if (currentUser.estGestionnaire()) {
                    ManagerDashboardController controller = loader.getController();
                    controller.setCurrentUser(currentUser);
                } else {
                    UserDashboardController controller = loader.getController();
                    controller.setCurrentUser(currentUser);
                }
            }

            javafx.stage.Stage stage = (javafx.stage.Stage) actionButton.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
