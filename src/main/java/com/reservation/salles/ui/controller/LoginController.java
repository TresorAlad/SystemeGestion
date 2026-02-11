package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.AuthService;
import com.reservation.salles.util.NotificationUtil;
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

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pwd = passwordField.getText();

        Utilisateur u = authService.login(email, pwd);
        if (u == null) {
            errorLabel.setText("Identifiants incorrects.");
            return;
        }

        NotificationUtil.succes("Bienvenue " + u.getNom());

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene;
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
            NotificationUtil.erreur("Erreur lors du chargement de l'écran suivant.");
        }
    }
}

