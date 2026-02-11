package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.SalleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerRoomsController {

    @FXML
    private Label currentUserLabel;

    @FXML
    private TableView<Salle> sallesTable;
    @FXML
    private TableColumn<Salle, String> colNom;
    @FXML
    private TableColumn<Salle, String> colType;
    @FXML
    private TableColumn<Salle, Integer> colCapacite;
    @FXML
    private TableColumn<Salle, Boolean> colDisponible;

    private final SalleService salleService = new SalleService();
    private final ObservableList<Salle> salles = FXCollections.observableArrayList();
    private Utilisateur currentUser;

    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
        chargerSalles();
    }

    @FXML
    private void initialize() {
        if (colNom != null) {
            colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
            colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
            sallesTable.setItems(salles);
        }
    }

    private void chargerSalles() {
        salles.setAll(salleService.listerToutesLesSalles());
    }

    @FXML
    private void handleGoDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
            Parent root = loader.load();
            ManagerDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) sallesTable.getScene().getWindow();
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

            Stage stage = (Stage) sallesTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToAddSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salle-form.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) sallesTable.getScene().getWindow();
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
            Stage stage = (Stage) sallesTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

