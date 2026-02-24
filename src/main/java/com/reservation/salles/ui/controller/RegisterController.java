package com.reservation.salles.ui.controller;

import com.reservation.salles.dao.UtilisateurDAO;
import com.reservation.salles.model.Utilisateur;
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

public class RegisterController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordVisibleField;
    @FXML
    private Label errorLabel;

    private boolean isPasswordVisible = false;
    private boolean isConfirmVisible = false;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    private void handleRegister(ActionEvent event) {
        String nom = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String pwd = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
        String confirm = isConfirmVisible ? confirmPasswordVisibleField.getText() : confirmPasswordField.getText();

        if (nom.isBlank() || email.isBlank() || pwd.isBlank() || confirm.isBlank()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }
        if (!pwd.equals(confirm)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }
        if (utilisateurDAO.findByEmail(email) != null) {
            errorLabel.setText("Cet email est déjà utilisé.");
            return;
        }

        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setEmail(email);
        u.setMotDePasse(pwd);
        u.setRole("UTILISATEUR");
        utilisateurDAO.save(u);

        NotificationUtil.showSuccess(
                (Stage) fullNameField.getScene().getWindow(),
                "Compte créé",
                "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter avec vos identifiants.",
                null);
    }

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

    @FXML
    private void toggleConfirmPassword() {
        if (isConfirmVisible) {
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordVisibleField.setManaged(false);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            isConfirmVisible = false;
        } else {
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordVisibleField.setVisible(true);
            confirmPasswordVisibleField.setManaged(true);
            isConfirmVisible = true;
        }
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        if (event == null) {
            return;
        }
        Node source = (Node) event.getSource();
        if (source == null || source.getScene() == null) {
            return;
        }
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Impossible d'ouvrir l'écran de connexion.");
        }
    }
}
