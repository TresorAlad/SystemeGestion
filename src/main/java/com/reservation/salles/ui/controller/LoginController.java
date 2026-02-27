package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour l'écran de connexion.
 * Gère l'authentification des utilisateurs et la redirection vers le tableau de
 * bord approprié.
 */
public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private Label errorLabel;

    private boolean isPasswordVisible = false;

    private final AuthService authService = new AuthService();

    /**
     * Tente de connecter l'utilisateur avec l'email et le mot de passe fournis.
     * En cas de succès, redirige vers le tableau de bord Utilisateur ou
     * Gestionnaire.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pwd = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();

        Utilisateur u = authService.login(email, pwd);
        if (u == null) {
            errorLabel.setText("Identifiants incorrects.");
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene;
            // Redirection conditionnelle basée sur le rôle
            if (u.estGestionnaire()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
                Parent root = loader.load();
                ManagerDashboardController controller = loader.getController();
                controller.setCurrentUser(u);
                scene = new Scene(root);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-dashboard.fxml"));
                Parent root = loader.load();
                UserDashboardController controller = loader.getController();
                controller.setCurrentUser(u);
                scene = new Scene(root);
            }
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de l'écran suivant.");
        }
    }

    /**
     * Alterne entre l'affichage masqué (●●●) et l'affichage clair du mot de passe.
     */
    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            isPasswordVisible = false;
        } else {
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            isPasswordVisible = true;
        }
    }

    /**
     * Redirige l'utilisateur vers l'écran d'inscription.
     */
    @FXML
    private void handleGoToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de l'écran d'inscription.");
        }
    }
}
