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
import javafx.stage.Popup;
import javafx.scene.input.MouseEvent;

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
    @FXML
    private TextField nomField;
    @FXML
    private TextField telephoneField;
    @FXML
    private TextField objetField;

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
    public void initialize() {
        // Rendre les champs non édifiables pour forcer l'utilisation du picker
        heureDebutField.setEditable(false);
        heureFinField.setEditable(false);

        // Ouvrir le picker au clic
        heureDebutField.setOnMouseClicked(this::showTimePicker);
        heureFinField.setOnMouseClicked(this::showTimePicker);

        // Valeur par défaut pour la date (aujourd'hui)
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    private void showTimePicker(MouseEvent event) {
        TextField field = (TextField) event.getSource();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/time-picker-popup.fxml"));
            Parent root = loader.load();

            Popup popup = new Popup();
            popup.getContent().add(root);
            popup.setAutoHide(true);

            TimePickerController controller = loader.getController();
            controller.init(popup, field);

            // Positionnement juste au-dessous du champ
            double x = field.localToScreen(0, 0).getX();
            double y = field.localToScreen(0, 0).getY() + field.getHeight();
            popup.show(field.getScene().getWindow(), x, y);

        } catch (IOException e) {
            e.printStackTrace();
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
            LocalTime debut = LocalTime.parse(heureDebutField.getText().replace(" ", ""));
            LocalTime fin = LocalTime.parse(heureFinField.getText().replace(" ", ""));

            if (!fin.isAfter(debut)) {
                NotificationUtil.info("L'heure de fin doit être après l'heure de début.");
                return;
            }

            String nom = nomField.getText();
            String telephone = telephoneField.getText();
            String objet = objetField.getText();

            if (nom.isEmpty() || telephone.isEmpty() || objet.isEmpty()) {
                NotificationUtil.info("Veuillez remplir tous les champs (Nom, Téléphone, Objet).");
                return;
            }

            Reservation r = reservationService.creerReservation(currentUser, salle, date, debut, fin, nom, telephone,
                    objet);
            if (r == null) {
                NotificationUtil.info("La salle n'est pas disponible pour ce créneau.");
                return;
            }

            if (currentUser.estGestionnaire()) {
                NotificationUtil.succes("Réservation effectuée avec succès.");
            } else {
                NotificationUtil.succes("Demande de réservation créée et envoyée au gestionnaire.");
            }
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
