package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Equipement;
import com.reservation.salles.model.Salle;
import com.reservation.salles.service.EquipementService;
import com.reservation.salles.service.SalleService;
import com.reservation.salles.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipementsFormController {

    @FXML
    private ComboBox<String> nom1Field;
    @FXML
    private TextField qte1Field;
    @FXML
    private ComboBox<String> nom2Field;
    @FXML
    private TextField qte2Field;
    @FXML
    private ComboBox<String> nom3Field;
    @FXML
    private TextField qte3Field;
    @FXML
    private ComboBox<String> nom4Field;
    @FXML
    private TextField qte4Field;
    @FXML
    private Label currentUserLabel;

    private com.reservation.salles.model.Utilisateur currentUser;
    private final EquipementService equipementService = new EquipementService();
    private final SalleService salleService = new SalleService();
    private Salle salleToSave;

    public void setSalle(Salle salle) {
        this.salleToSave = salle;
    }

    @FXML
    public void initialize() {
        loadEquipementOptions();
    }

    private void loadEquipementOptions() {
        List<Equipement> existing = equipementService.listerEquipements();
        List<String> names = existing.stream().map(Equipement::getNom).distinct().toList();

        nom1Field.getItems().addAll(names);
        nom2Field.getItems().addAll(names);
        nom3Field.getItems().addAll(names);
        nom4Field.getItems().addAll(names);
    }

    public void setCurrentUser(com.reservation.salles.model.Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
    }

    @FXML
    private void handleAjouter() {
        List<Equipement> equipements = new ArrayList<>();
        ajouterDepuisChamps(nom1Field, qte1Field, equipements);
        ajouterDepuisChamps(nom2Field, qte2Field, equipements);
        ajouterDepuisChamps(nom3Field, qte3Field, equipements);
        ajouterDepuisChamps(nom4Field, qte4Field, equipements);

        if (equipements.isEmpty()) {
            NotificationUtil.showPage(nom1Field, "Aucun équipement",
                    "Veuillez ajouter au moins un équipement avant de valider.", currentUser, false);
            return;
        }

        if (salleToSave == null) {
            NotificationUtil.showPage(nom1Field, "Erreur de données",
                    "Les informations de la salle sont manquantes. Veuillez recommencer.", currentUser, false);
            return;
        }

        // Sauvegarder la salle d'abord
        if (salleToSave.getIdSalle() == 0) {
            salleService.ajouterSalle(salleToSave);
        } else {
            salleService.modifierSalle(salleToSave);
        }

        equipementService.enregistrerEquipements(equipements);

        String actionType = (salleToSave.getIdSalle() == 0) ? "créée" : "modifiée";
        NotificationUtil.showSuccess(
                (javafx.stage.Stage) nom1Field.getScene().getWindow(),
                "Opération terminée",
                "La salle '" + salleToSave.getNom() + "' a été " + actionType
                        + " avec succès ainsi que ses équipements.",
                currentUser);
    }

    private void ajouterDepuisChamps(ComboBox<String> nomField, TextField qteField, List<Equipement> equipements) {
        String nom = nomField.getValue();
        String qteStr = qteField.getText();
        if (nom != null && !nom.isBlank() && qteStr != null && !qteStr.isBlank()) {
            try {
                int qte = Integer.parseInt(qteStr);
                equipements.add(new Equipement(0, nom, qte));
            } catch (NumberFormatException e) {
                // on ignore les lignes invalides mais on pourrait aussi notifier
            }
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
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            Stage stage = (Stage) nom1Field.getScene().getWindow();
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
            Stage stage = (Stage) nom1Field.getScene().getWindow();
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
            Stage stage = (Stage) nom1Field.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salle-form.fxml"));
            Parent root = loader.load();
            SalleFormController controller = loader.getController();
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }
            Stage stage = (Stage) nom1Field.getScene().getWindow();
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

            Stage stage = (Stage) nom1Field.getScene().getWindow();
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
            Stage stage = (Stage) nom1Field.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
