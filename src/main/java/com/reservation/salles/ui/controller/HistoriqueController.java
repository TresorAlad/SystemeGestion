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
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HistoriqueController {

    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> colId;
    @FXML
    private TableColumn<Reservation, String> colSalle;
    @FXML
    private TableColumn<Reservation, String> colDate;
    @FXML
    private TableColumn<Reservation, String> colHeure;
    @FXML
    private TableColumn<Reservation, String> colStatut;
    @FXML
    private TableColumn<Reservation, String> colActions;

    private Utilisateur currentUser;
    private final ReservationService reservationService = new ReservationService();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    public void initData(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        chargerReservations();
    }

    @FXML
    private void initialize() {
        if (colSalle != null) {
            reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            colId.setCellValueFactory(
                    cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getIdReservation())));
            colSalle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSalle().getNom()));
            colDate.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getDate() != null ? cell.getValue().getDate().toString() : ""));
            colHeure.setCellValueFactory(cell -> new SimpleStringProperty(
                    (cell.getValue().getHeureDebut() != null ? cell.getValue().getHeureDebut() : "") + " - " +
                            (cell.getValue().getHeureFin() != null ? cell.getValue().getHeureFin() : "")));
            colStatut.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getStatut() != null ? cell.getValue().getStatut() : ""));

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

            // Colonne Actions avec boutons
            colActions.setCellFactory(column -> new TableCell<Reservation, String>() {
                private final Button annulerBtn = new Button("Annuler");

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        return;
                    }

                    Reservation reservation = getTableRow().getItem();
                    annulerBtn.setStyle("-fx-padding: 4 12 4 12; -fx-font-size: 11px;");
                    annulerBtn.setOnAction(event -> {
                        if ("VALIDEE".equals(reservation.getStatut()) || "EN_ATTENTE".equals(reservation.getStatut())) {
                            reservationService.annulerReservation(reservation);
                            NotificationUtil.info("Réservation annulée.");
                            chargerReservations();
                        } else {
                            NotificationUtil.info("Cette réservation ne peut plus être annulée.");
                        }
                    });

                    if ("VALIDEE".equals(reservation.getStatut()) || "EN_ATTENTE".equals(reservation.getStatut())) {
                        annulerBtn.setStyle(
                                "-fx-padding: 4 12 4 12; -fx-font-size: 11px; -fx-background-color: #f87171;");
                        setGraphic(annulerBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            });

            reservationsTable.setItems(reservations);
        }
    }

    private void chargerReservations() {
        if (currentUser != null) {
            try {
                reservations.setAll(reservationService.listerParUtilisateur(currentUser.getIdUtilisateur()));
            } catch (Exception e) {
                e.printStackTrace();
                NotificationUtil.erreur("Erreur lors du chargement des réservations: " + e.getMessage());
            }
        }
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
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-dashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
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
    private void handleGoDashboard() {
        handleRetour();
    }
}
