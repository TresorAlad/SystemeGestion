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
            NotificationUtil.showPage(salleLabel, "Date manquante", "Veuillez choisir une date pour votre réservation.",
                    currentUser, false);
            return;
        }
        try {
            LocalTime debut = LocalTime.parse(heureDebutField.getText());
            LocalTime fin = LocalTime.parse(heureFinField.getText());

            if (!fin.isAfter(debut)) {
                NotificationUtil.showPage(salleLabel, "Horaire invalide",
                        "L'heure de fin doit être après l'heure de début.", currentUser, false);
                return;
            }

            Reservation r = reservationService.creerReservation(currentUser, salle, date, debut, fin);
            if (r == null) {
                NotificationUtil.showPage(salleLabel, "Salle indisponible",
                        "La salle n'est pas disponible pour ce créneau horaire.", currentUser, false);
                return;
            }

            if (currentUser.estGestionnaire()) {
                NotificationUtil.showSuccess(
                        (javafx.stage.Stage) salleLabel.getScene().getWindow(),
                        "Réservation confirmée",
                        "La réservation pour la salle '" + salle.getNom() + "' a été effectuée avec succès.",
                        currentUser);
            } else {
                NotificationUtil.showSuccess(
                        (javafx.stage.Stage) salleLabel.getScene().getWindow(),
                        "Demande envoyée",
                        "Votre demande de réservation pour la salle '" + salle.getNom()
                                + "' a été transmise au gestionnaire.",
                        currentUser);
            }

        } catch (DateTimeParseException e) {
            NotificationUtil.showPage(salleLabel, "Format invalide", "Le format d'heure est invalide (attendu HH:MM).",
                    currentUser, false);
        }
    }

    @FXML
    private void handleRetour() {
        retourDashboard();
    }

    private void retourDashboard() {
        try {
            boolean isManager = currentUser != null && currentUser.estGestionnaire();
            String fxml = isManager ? "/fxml/manager-dashboard.fxml" : "/fxml/user-dashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (isManager) {
                ManagerDashboardController controller = loader.getController();
                controller.setCurrentUser(currentUser);
            } else {
                UserDashboardController controller = loader.getController();
                controller.setCurrentUser(currentUser);
            }

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
            boolean isManager = currentUser != null && currentUser.estGestionnaire();
            String fxml = isManager ? "/fxml/reservations-list.fxml" : "/fxml/historique.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (isManager) {
                ReservationsListController controller = loader.getController();
                controller.initData(currentUser);
            } else {
                HistoriqueController controller = loader.getController();
                controller.initData(currentUser);
            }

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
