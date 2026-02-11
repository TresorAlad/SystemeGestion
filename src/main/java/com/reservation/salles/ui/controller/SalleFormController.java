package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Salle;
import com.reservation.salles.service.SalleService;
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

    private final SalleService salleService = new SalleService();
    private String photoRelativePath;

    @FXML
    private void handleChoisirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de salle");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) nomField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        try {
            // dossier de destination dans le projet
            Path photosDir = Path.of("photos");
            if (!Files.exists(photosDir)) {
                Files.createDirectories(photosDir);
            }
            String targetName = "salle_" + System.currentTimeMillis() + "_" + file.getName();
            Path target = photosDir.resolve(targetName);
            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            photoRelativePath = target.toString(); // ex: photos/salle_xxx.jpg
            if (photoPathLabel != null) {
                photoPathLabel.setText(targetName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.erreur("Impossible de copier l'image.");
        }
    }

    @FXML
    private void handleSuivant() {
        String nom = nomField.getText();
        String type = typeField.getText();
        int capacite;
        try {
            capacite = Integer.parseInt(capaciteField.getText());
        } catch (NumberFormatException e) {
            NotificationUtil.info("Capacité invalide.");
            return;
        }
        if (nom == null || nom.isBlank() || type == null || type.isBlank()) {
            NotificationUtil.info("Veuillez remplir les champs obligatoires.");
            return;
        }

        Salle salle = new Salle(0, nom, type, capacite, true);
        salle.setPhoto(photoRelativePath != null ? photoRelativePath : "jav.jpg");
        salleService.ajouterSalle(salle);
        NotificationUtil.succes("Salle créée.");

        // Aller vers l'écran d'équipements
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/equipements-form.fxml"));
            Parent root = loader.load();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
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

