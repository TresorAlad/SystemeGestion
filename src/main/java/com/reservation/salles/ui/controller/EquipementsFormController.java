package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Equipement;
import com.reservation.salles.service.EquipementService;
import com.reservation.salles.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipementsFormController {

    @FXML
    private TextField nom1Field;
    @FXML
    private TextField qte1Field;
    @FXML
    private TextField nom2Field;
    @FXML
    private TextField qte2Field;
    @FXML
    private TextField nom3Field;
    @FXML
    private TextField qte3Field;
    @FXML
    private TextField nom4Field;
    @FXML
    private TextField qte4Field;

    private final EquipementService equipementService = new EquipementService();

    @FXML
    private void handleAjouter() {
        List<Equipement> equipements = new ArrayList<>();
        ajouterDepuisChamps(nom1Field, qte1Field, equipements);
        ajouterDepuisChamps(nom2Field, qte2Field, equipements);
        ajouterDepuisChamps(nom3Field, qte3Field, equipements);
        ajouterDepuisChamps(nom4Field, qte4Field, equipements);

        if (equipements.isEmpty()) {
            NotificationUtil.info("Aucun équipement à enregistrer.");
            return;
        }

        equipementService.enregistrerEquipements(equipements);
        NotificationUtil.succes("Equipements enregistrés.");
        retournerDashboard();
    }

    private void ajouterDepuisChamps(TextField nomField, TextField qteField, List<Equipement> equipements) {
        String nom = nomField.getText();
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
        retournerDashboard();
    }

    private void retournerDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
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

