package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Reservation;
import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.ReservationService;
import com.reservation.salles.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ReservationFormController {

    @FXML
    private Label currentUserLabel;
    @FXML
    private Label salleLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField heureDebutField;
    @FXML
    private TextField heureFinField;

    private Utilisateur currentUser;
    private Salle salle;

    private final ReservationService reservationService = new ReservationService();

    public void initData(Utilisateur utilisateur, Salle salle) {
        this.currentUser = utilisateur;
        this.salle = salle;
        this.salleLabel.setText(salle.getNom());
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
    }

    @FXML
    private void handleValider() {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            NotificationUtil.info("Veuillez choisir une date.");
            return;
        }
        try {
            LocalTime debut = LocalTime.parse(heureDebutField.getText());
            LocalTime fin = LocalTime.parse(heureFinField.getText());

            if (!fin.isAfter(debut)) {
                NotificationUtil.info("L'heure de fin doit être après l'heure de début.");
                return;
            }

            Reservation r = reservationService.creerReservation(currentUser, salle, date, debut, fin);
            if (r == null) {
                NotificationUtil.info("La salle n'est pas disponible pour ce créneau.");
                return;
            }

            NotificationUtil.succes("Demande de réservation créée et envoyée au gestionnaire.");
            retourDashboard();

        } catch (DateTimeParseException e) {
            NotificationUtil.info("Format d'heure invalide (attendu HH:MM).");
        }
    }

    @FXML
    private void handleRetour() {
        retourDashboard();
    }

    private void retourDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-dashboard.fxml"));
            Parent root = loader.load();
            UserDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) salleLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoDashboard() {
        retourDashboard();
    }

    @FXML
    private void handleGoHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservations-list.fxml"));
            Parent root = loader.load();
            ReservationsListController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) salleLabel.getScene().getWindow();
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

            Stage stage = (Stage) salleLabel.getScene().getWindow();
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
            Stage stage = (Stage) salleLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
