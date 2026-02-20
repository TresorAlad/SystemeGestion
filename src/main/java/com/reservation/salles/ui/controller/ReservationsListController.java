package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Reservation;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.ReservationService;
import com.reservation.salles.util.NotificationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class ReservationsListController {

    @FXML
    private Label currentUserLabel;

    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> colSalle;
    @FXML
    private TableColumn<Reservation, String> colDate;
    @FXML
    private TableColumn<Reservation, String> colHeure;
    @FXML
    private TableColumn<Reservation, String> colStatut;

    private Utilisateur currentUser;
    private final ReservationService reservationService = new ReservationService();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    public void initData(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
        chargerReservations();
    }

    @FXML
    private void initialize() {
        if (colSalle != null) {
            colSalle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSalle().getNom()));
            colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
            colHeure.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getHeureDebut() + " - " + cell.getValue().getHeureFin()));
            colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));

            // Cellule personnalisée pour afficher un badge coloré selon le statut
            colStatut.setCellFactory(column -> new TableCell<Reservation, String>() {
                private final Label label = new Label();

                @Override
                protected void updateItem(String statut, boolean empty) {
                    super.updateItem(statut, empty);
                    if (empty || statut == null) {
                        setGraphic(null);
                        return;
                    }

                    label.getStyleClass().setAll("status-chip"); // reset
                    String upper = statut.toUpperCase();
                    if ("VALIDEE".equals(upper) || "CONFIRMEE".equals(upper)) {
                        label.getStyleClass().add("status-chip-success");
                    } else if ("EN_ATTENTE".equals(upper)) {
                        label.getStyleClass().add("status-chip-pending");
                    } else if ("REJETEE".equals(upper) || "ANNULEE".equals(upper)) {
                        label.getStyleClass().add("status-chip-danger");
                    }
                    label.setText(upper);
                    setGraphic(label);
                }
            });
            reservationsTable.setItems(reservations);
        }
    }

    private void chargerReservations() {
        reservations.setAll(reservationService.listerParUtilisateur(currentUser.getIdUtilisateur()));
    }

    @FXML
    private void handleAnnuler() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtil.info("Sélectionnez une réservation.");
            return;
        }
        if ("VALIDEE".equals(selected.getStatut()) || "EN_ATTENTE".equals(selected.getStatut())) {
            reservationService.annulerReservation(selected);
            NotificationUtil.info("Réservation annulée.");
            chargerReservations();
        } else {
            NotificationUtil.info("Cette réservation ne peut plus être annulée.");
        }
    }

    @FXML
    private void handleGoDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-dashboard.fxml"));
            Parent root = loader.load();
            ManagerDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        handleGoDashboard();
    }

    @FXML
    private void handleMesSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-rooms.fxml"));
            Parent root = loader.load();
            ManagerRoomsController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
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
            SalleFormController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
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
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
