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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

public class HistoriqueController {

    @FXML
    private Label currentUserLabel;

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
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
        chargerReservations();
    }

    @FXML
    private void initialize() {
        if (colSalle != null) {
            reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            colId.setCellValueFactory(
                    cell -> new SimpleStringProperty(
                            cell.getValue() != null ? String.valueOf(cell.getValue().getIdReservation()) : ""));
            colSalle.setCellValueFactory(cell -> new SimpleStringProperty(
                    (cell.getValue() != null && cell.getValue().getSalle() != null)
                            ? cell.getValue().getSalle().getNom()
                            : "N/A"));
            colDate.setCellValueFactory(cell -> new SimpleStringProperty(
                    (cell.getValue() != null && cell.getValue().getDate() != null)
                            ? cell.getValue().getDate().toString()
                            : ""));
            colHeure.setCellValueFactory(cell -> new SimpleStringProperty(
                    (cell.getValue() != null && cell.getValue().getHeureDebut() != null
                            ? cell.getValue().getHeureDebut()
                            : "") + " - " +
                            (cell.getValue() != null && cell.getValue().getHeureFin() != null
                                    ? cell.getValue().getHeureFin()
                                    : "")));
            colStatut.setCellValueFactory(cell -> new SimpleStringProperty(
                    (cell.getValue() != null && cell.getValue().getStatut() != null) ? cell.getValue().getStatut()
                            : ""));

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
                private final Button detailBtn = new Button("Détails");
                private final Button annulerBtn = new Button("Annuler");
                private final HBox container = new HBox(8, detailBtn, annulerBtn);

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        return;
                    }

                    Reservation r = getTableRow().getItem();

                    detailBtn.getStyleClass().setAll("secondary-button");
                    detailBtn.setStyle("-fx-padding: 4 12 4 12; -fx-font-size: 11px;");
                    detailBtn.setOnAction(event -> {
                        String msg = String.format("Détails de la réservation #%d :\n" +
                                "Client : %s\n" +
                                "Salle : %s\n" +
                                "Date : %s\n" +
                                "Horaire : %s - %s\n" +
                                "Statut : %s",
                                r.getIdReservation(),
                                r.getUtilisateur() != null ? r.getUtilisateur().getNom() : "Inconnu",
                                r.getSalle() != null ? r.getSalle().getNom() : "Inconnue",
                                r.getDate() != null ? r.getDate() : "Non définie",
                                r.getHeureDebut() != null ? r.getHeureDebut() : "?",
                                r.getHeureFin() != null ? r.getHeureFin() : "?",
                                r.getStatut() != null ? r.getStatut() : "Inconnu");
                        NotificationUtil.showPage(reservationsTable, "Détails de réservation", msg, currentUser, true);
                    });

                    annulerBtn.getStyleClass().setAll("danger-button");
                    annulerBtn.setStyle("-fx-padding: 4 12 4 12; -fx-font-size: 11px;");
                    annulerBtn.setOnAction(event -> {
                        if ("VALIDEE".equals(r.getStatut()) || "EN_ATTENTE".equals(r.getStatut())) {
                            reservationService.annulerReservation(r);
                            NotificationUtil.showSuccess(
                                    (Stage) reservationsTable.getScene().getWindow(),
                                    "Réservation Annulée",
                                    "La réservation pour '" + (r.getSalle() != null ? r.getSalle().getNom()
                                            : "#" + r.getIdReservation()) + "' a été annulée.",
                                    currentUser);
                        } else {
                            NotificationUtil.showPage(reservationsTable, "Action impossible",
                                    "Cette réservation ne peut plus être annulée.", currentUser, false);
                        }
                    });

                    // On n'affiche le bouton annuler que si c'est possible
                    if ("VALIDEE".equals(r.getStatut()) || "EN_ATTENTE".equals(r.getStatut())) {
                        annulerBtn.setVisible(true);
                    } else {
                        annulerBtn.setVisible(false);
                    }

                    setGraphic(container);
                }
            });

            reservationsTable.setItems(reservations);
        }
    }

    private void chargerReservations() {
        if (currentUser != null) {
            try {
                if (currentUser.estGestionnaire()) {
                    reservations.setAll(reservationService.listerToutesLesReservations());
                } else {
                    reservations.setAll(reservationService.listerParUtilisateur(currentUser.getIdUtilisateur()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                NotificationUtil.showPage(reservationsTable, "Erreur de chargement",
                        "Impossible de récupérer vos réservations : " + e.getMessage(), currentUser, false);
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtil.showPage(reservationsTable, "Sélection requise",
                    "Veuillez sélectionner une réservation dans la liste.", currentUser, false);
            return;
        }
        if ("VALIDEE".equals(selected.getStatut()) || "EN_ATTENTE".equals(selected.getStatut())) {
            reservationService.annulerReservation(selected);
            NotificationUtil.showSuccess(
                    (Stage) reservationsTable.getScene().getWindow(),
                    "Réservation Annulée",
                    "La réservation a été annulée avec succès.",
                    currentUser);
        } else {
            NotificationUtil.showPage(reservationsTable, "Annulation refusée",
                    "Le statut actuel de cette réservation ne permet pas son annulation.", currentUser, false);
        }
    }

    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profil.fxml"));
            Parent root = loader.load();
            ProfilController controller = loader.getController();
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
        if (currentUser == null)
            return;
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

            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showPage(reservationsTable, "Erreur de navigation",
                    "Impossible de retourner au tableau de bord.", currentUser, false);
        }
    }

    @FXML
    private void handleGoDashboard() {
        handleRetour();
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
