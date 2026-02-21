package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Reservation;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.ReservationService;
import com.reservation.salles.service.SalleService;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerDashboardController {

    @FXML
    private Label currentUserLabel;

    @FXML
    private Label reservationsActivesLabel;
    @FXML
    private Label sallesDisponiblesLabel;

    @FXML
    private TableView<Reservation> recentesTable;
    @FXML
    private TableColumn<Reservation, String> colSalle;
    @FXML
    private TableColumn<Reservation, String> colDate;
    @FXML
    private TableColumn<Reservation, String> colHeure;
    @FXML
    private TableColumn<Reservation, String> colStatut;
    @FXML
    private TableColumn<Reservation, Void> colActions;

    private Utilisateur currentUser;
    private final ReservationService reservationService = new ReservationService();
    private final SalleService salleService = new SalleService();
    private final ObservableList<Reservation> reservationsRecentes = FXCollections.observableArrayList();

    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        currentUserLabel.setText(utilisateur.getNom());
        chargerDashboard();
    }

    @FXML
    private void initialize() {
        if (colSalle != null) {
            colSalle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSalle().getNom()));
            colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
            colHeure.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getHeureDebut() + " - " + cell.getValue().getHeureFin()));
            colStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut()));

            configurerStatut();
            configurerActions();

            recentesTable.setItems(reservationsRecentes);
        }
    }

    private void configurerStatut() {
        colStatut.setCellFactory(column -> new TableCell<>() {
            private final Label label = new Label();

            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setGraphic(null);
                } else {
                    label.getStyleClass().setAll("status-chip");
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
            }
        });
    }

    private void configurerActions() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnValider = new Button("Accepter");
            private final Button btnRejeter = new Button("Rejeter");
            private final HBox container = new HBox(8, btnValider, btnRejeter);

            {
                btnValider.getStyleClass().add("primary-button");
                btnRejeter.getStyleClass().add("danger-button");

                btnValider.setOnAction(event -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    reservationService.validerReservation(r);
                    recentesTable.refresh();
                    chargerDashboard();
                });

                btnRejeter.setOnAction(event -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    reservationService.rejeterReservation(r);
                    recentesTable.refresh();
                    chargerDashboard();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation r = getTableView().getItems().get(getIndex());
                    if ("EN_ATTENTE".equals(r.getStatut())) {
                        setGraphic(container);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void chargerDashboard() {
        // métriques
        int actives = reservationService.compterReservationsActives();
        int dispos = salleService.compterDisponibles();
        if (reservationsActivesLabel != null) {
            reservationsActivesLabel.setText(String.valueOf(actives));
        }
        if (sallesDisponiblesLabel != null) {
            sallesDisponiblesLabel.setText(String.valueOf(dispos));
        }
        // réservations récentes
        reservationsRecentes.setAll(reservationService.listerRecentes(10));
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
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
            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
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
            if (controller == null) {
                System.err.println("ERREUR: Le contrôleur ReservationsListController est null");
                return;
            }
            controller.initData(currentUser);

            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            String errMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            System.err.println("ERREUR DÉTAILLÉE: " + errMsg);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleMesSalles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manager-rooms.fxml"));
            Parent root = loader.load();
            ManagerRoomsController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
