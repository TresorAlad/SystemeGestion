package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.UtilisateurService;
import com.reservation.salles.util.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProfilController {

    @FXML
    private Label currentUserLabelTop;
    @FXML
    private TextField nomField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label emailLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private VBox adminSection;
    @FXML
    private ComboBox<Utilisateur> userComboBox;

    private Utilisateur currentUser;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        chargerInfos();
    }

    private void chargerInfos() {
        if (currentUser == null)
            return;

        currentUserLabelTop.setText(currentUser.getNom());
        nomField.setText(currentUser.getNom());
        emailLabel.setText(currentUser.getEmail());
        roleLabel.setText(currentUser.getRole());
        passwordField.setText(currentUser.getMotDePasse());

        if (currentUser.estGestionnaire()) {
            adminSection.setVisible(true);
            chargerListeUtilisateurs();
        }
    }

    private void chargerListeUtilisateurs() {
        List<Utilisateur> allUsers = utilisateurService.listerTousLesUtilisateurs();
        // Filtrer pour ne montrer que les UTILISATEURS simples
        List<Utilisateur> simpleUsers = allUsers.stream()
                .filter(u -> u.estUtilisateur())
                .collect(Collectors.toList());

        userComboBox.setItems(FXCollections.observableArrayList(simpleUsers));
        userComboBox.setConverter(new StringConverter<Utilisateur>() {
            @Override
            public String toString(Utilisateur u) {
                return u == null ? "" : u.getNom() + " (" + u.getEmail() + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handlePromouvoir() {
        Utilisateur selected = userComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtil.info("Veuillez sélectionner un utilisateur.");
            return;
        }

        utilisateurService.promouvoirEnGestionnaire(selected.getIdUtilisateur());
        NotificationUtil.info("L'utilisateur " + selected.getNom() + " a été promu Gestionnaire.");
        chargerListeUtilisateurs();
    }

    @FXML
    private void handleSauvegarder() {
        String nouveauNom = nomField.getText();
        String nouveauPass = passwordField.getText();

        if (nouveauNom == null || nouveauNom.isBlank() || nouveauPass == null || nouveauPass.isBlank()) {
            NotificationUtil.info("Le nom et le mot de passe ne peuvent pas être vides.");
            return;
        }

        utilisateurService.mettreAJourProfil(currentUser.getIdUtilisateur(), nouveauNom, nouveauPass);

        // Mettre à jour l'objet local
        currentUser.setNom(nouveauNom);
        currentUser.setMotDePasse(nouveauPass);

        currentUserLabelTop.setText(nouveauNom);
        NotificationUtil.succes("Profil mis à jour avec succès.");
    }

    @FXML
    private void handleRetour() {
        try {
            String fxmlFile = currentUser.estGestionnaire() ? "/fxml/manager-dashboard.fxml"
                    : "/fxml/user-dashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (currentUser.estGestionnaire()) {
                ManagerDashboardController controller = loader.getController();
                controller.setCurrentUser(currentUser);
            } else {
                UserDashboardController controller = loader.getController();
                controller.setCurrentUser(currentUser);
            }

            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
