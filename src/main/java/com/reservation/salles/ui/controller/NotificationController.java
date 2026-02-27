package com.reservation.salles.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Contrôleur pour la vue de notification.
 * Affiche des messages de succès ou d'erreur avec un titre, une description et
 * une action.
 */
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

    /**
     * Initialise la vue en mode Succès (icône verte, titre et description fournis).
     */
    public void initSuccess(String message, String description, com.reservation.salles.model.Utilisateur user) {
        this.currentUser = user;
        iconLabel.setText("✓");
        iconLabel.getStyleClass().add("icon-success");
        messageLabel.setText(message);
        descriptionLabel.setText(description);
        actionButton.setText("Continuer");
    }

    /**
     * Initialise la vue en mode Erreur (icône rouge, titre et description fournis).
     */
    public void initError(String message, String description, com.reservation.salles.model.Utilisateur user) {
        this.currentUser = user;
        iconLabel.setText("✕");
        iconLabel.getStyleClass().add("icon-error");
        messageLabel.setText(message);
        descriptionLabel.setText(description);
        actionButton.setText("Réessayer");
    }

    /**
     * Gère l'action du bouton (redirection vers le dashboard ou l'écran de
     * connexion).
     */
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

    /**
     * Ferme la fenêtre actuelle de notification.
     */
    public void close() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}
