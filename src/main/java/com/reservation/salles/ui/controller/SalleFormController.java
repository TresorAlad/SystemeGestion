package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Salle;
import com.reservation.salles.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Contrôleur pour le formulaire de création ou modification d'une salle.
 * Gère également l'upload d'image pour illustrer la salle.
 */
public class SalleFormController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField capaciteField;
    @FXML
    private TextField typeField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label photoPathLabel;
    @FXML
    private Label currentUserLabel;

    private String photoRelativePath;
    private com.reservation.salles.model.Utilisateur currentUser;
    private Salle salleAmodifier;

    /**
     * Initialise le formulaire en mode édition si une salle est fournie.
     */
    public void setSalle(Salle salle) {
        this.salleAmodifier = salle;
        if (salle != null) {
            nomField.setText(salle.getNom());
            typeField.setText(salle.getType());
            capaciteField.setText(String.valueOf(salle.getCapacite()));
            photoRelativePath = salle.getPhoto();
            if (photoPathLabel != null && photoRelativePath != null) {
                photoPathLabel.setText(photoRelativePath);
            }
        }
    }

    public void setCurrentUser(com.reservation.salles.model.Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
    }

    /**
     * Gère la sélection d'une image locale et sa copie dans le dossier 'photos' du
     * projet.
     */
    @FXML
    private void handleChoisirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de salle");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        Stage stage = (Stage) nomField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        try {
            // Dossier de destination relatif au projet
            Path photosDir = Path.of("photos");
            if (!Files.exists(photosDir)) {
                Files.createDirectories(photosDir);
            }
            String targetName = "salle_" + System.currentTimeMillis() + "_" + file.getName();
            Path target = photosDir.resolve(targetName);
            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            photoRelativePath = target.toString(); // ex: photos/salle_123.jpg
            if (photoPathLabel != null) {
                photoPathLabel.setText(targetName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showPage(nomField, "Erreur d'image", "Impossible de copier l'image sélectionnée.",
                    currentUser, false);
        }
    }

    /**
     * Valide les données de la salle et passe à l'étape suivante (Equipements).
     */
    @FXML
    private void handleSuivant() {
        String nom = nomField.getText();
        String type = typeField.getText();
        int capacite;
        try {
            capacite = Integer.parseInt(capaciteField.getText());
        } catch (NumberFormatException e) {
            NotificationUtil.showPage(nomField, "Capacité invalide",
                    "Veuillez saisir un nombre valide pour la capacité.", currentUser, false);
            return;
        }
        if (nom == null || nom.isBlank() || type == null || type.isBlank()) {
            NotificationUtil.showPage(nomField, "Champs manquants", "Veuillez remplir le nom et le type de salle.",
                    currentUser, false);
            return;
        }

        Salle salleToProcess;
        if (salleAmodifier == null) {
            salleToProcess = new Salle(0, nom, type, capacite, true);
            salleToProcess.setPhoto(photoRelativePath != null ? photoRelativePath : "jav.jpg");
        } else {
            salleAmodifier.setNom(nom);
            salleAmodifier.setType(type);
            salleAmodifier.setCapacite(capacite);
            salleAmodifier.setPhoto(photoRelativePath != null ? photoRelativePath : "jav.jpg");
            salleToProcess = salleAmodifier;
        }

        // Redirection vers le formulaire des équipements
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/equipements-form.fxml"));
            Parent root = loader.load();

            EquipementsFormController controller = loader.getController();
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            controller.setSalle(salleToProcess);

            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        handleGoDashboard();
    }

    @FXML
    private void handleGoDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
            Parent root = loader.load();
            ManagerDashboardController controller = loader.getController();
            if (currentUser != null)
                controller.setCurrentUser(currentUser);
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMesReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservations-list.fxml"));
            Parent root = loader.load();
            ReservationsListController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMesSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-rooms.fxml"));
            Parent root = loader.load();
            ManagerRoomsController controller = loader.getController();
            if (currentUser != null)
                controller.setCurrentUser(currentUser);
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profil.fxml"));
            Parent root = loader.load();
            ProfilController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
