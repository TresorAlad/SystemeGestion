package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.model.Equipement;
import com.reservation.salles.service.SalleService;
import com.reservation.salles.service.EquipementService;
import com.reservation.salles.util.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ManagerRoomsController {

    @FXML
    private Label currentUserLabel;
    @FXML
    private Label totalSallesLabel;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane sallesFlow;

    private final SalleService salleService = new SalleService();
    private final EquipementService equipementService = new EquipementService();
    private Utilisateur currentUser;
    private final ObservableList<Salle> toutesLesSalles = FXCollections.observableArrayList();
    private Map<String, Integer> quantitesEquipements = new HashMap<>();

    // ---------------------------------------------------------------
    // Init
    // ---------------------------------------------------------------
    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        if (currentUserLabel != null && utilisateur != null) {
            currentUserLabel.setText(utilisateur.getNom());
        }
        chargerSalles();
        rafraichirCartes(toutesLesSalles);
    }

    @FXML
    private void initialize() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> appliquerFiltre(newV));
        }
    }

    // ---------------------------------------------------------------
    // Données
    // ---------------------------------------------------------------
    private void chargerSalles() {
        toutesLesSalles.setAll(salleService.listerToutesLesSalles());
        quantitesEquipements.clear();
        for (Equipement e : equipementService.listerEquipements()) {
            quantitesEquipements.put(e.getNom(), e.getQuantite());
        }
        if (totalSallesLabel != null) {
            totalSallesLabel.setText("Mes salles (" + toutesLesSalles.size() + ")");
        }
    }

    private void appliquerFiltre(String texte) {
        if (texte == null || texte.isBlank()) {
            rafraichirCartes(toutesLesSalles);
            return;
        }
        String lower = texte.toLowerCase(Locale.ROOT);
        List<Salle> filtre = toutesLesSalles.stream()
                .filter(s -> s.getNom().toLowerCase(Locale.ROOT).contains(lower)
                        || s.getType().toLowerCase(Locale.ROOT).contains(lower))
                .collect(Collectors.toList());
        rafraichirCartes(FXCollections.observableArrayList(filtre));
    }

    // ---------------------------------------------------------------
    // Affichage des cartes
    // ---------------------------------------------------------------
    private void rafraichirCartes(List<Salle> salles) {
        if (sallesFlow == null)
            return;
        sallesFlow.getChildren().clear();
        for (Salle salle : salles) {
            sallesFlow.getChildren().add(creerCarteSalle(salle));
        }
    }

    private VBox creerCarteSalle(Salle salle) {
        VBox card = new VBox(8);
        card.getStyleClass().add("room-card");

        // --- Image avec badge disponibilité ---
        StackPane imagePane = new StackPane();
        imagePane.getStyleClass().add("room-image");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:jav.jpg", 210, 120, true, true);
            imageView.setImage(image);
        } catch (Exception ignored) {
        }
        imageView.setFitWidth(210);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Rectangle clip = new Rectangle(210, 120);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);

        Label badge = new Label(salle.isDisponible() ? "Disponible" : "Indisponible");
        badge.getStyleClass().addAll("availability-badge",
                salle.isDisponible() ? "availability-badge-on" : "availability-badge-off");
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(8, 8, 0, 0));

        imagePane.getChildren().addAll(imageView, badge);

        // --- Infos de la salle ---
        Label nomLabel = new Label(salle.getNom());
        nomLabel.getStyleClass().add("room-name");

        Label typeLabel = new Label(salle.getType());
        typeLabel.getStyleClass().add("room-meta");

        Label capaLabel = new Label(salle.getCapacite() + " personnes");
        capaLabel.getStyleClass().add("room-meta");

        // --- Chips équipements ---
        HBox chips = new HBox(6);
        chips.setAlignment(Pos.CENTER);
        chips.getChildren().addAll(
                creerChip("Projecteur"),
                creerChip("WiFi"),
                creerChip("Tableau blanc"));

        // --- Boutons d'action ---
        VBox cardActions = new VBox(8);
        cardActions.setAlignment(Pos.CENTER);

        Button reserverBtn = new Button("Réserver");
        reserverBtn.getStyleClass().add("primary-button");
        reserverBtn.setMaxWidth(Double.MAX_VALUE);
        reserverBtn.setOnAction(e -> ouvrirReservation(salle));

        HBox managerActions = new HBox(8);
        managerActions.setAlignment(Pos.CENTER);

        Button modifierBtn = new Button("✏ Modifier");
        modifierBtn.getStyleClass().add("secondary-button");
        modifierBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(modifierBtn, javafx.scene.layout.Priority.ALWAYS);
        modifierBtn.setOnAction(e -> ouvrirModification(salle));

        Button supprimerBtn = new Button("🗑 Supprimer");
        supprimerBtn.getStyleClass().add("btn-danger");
        supprimerBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(supprimerBtn, javafx.scene.layout.Priority.ALWAYS);
        supprimerBtn.setOnAction(e -> supprimerSalle(salle));

        managerActions.getChildren().addAll(modifierBtn, supprimerBtn);
        cardActions.getChildren().addAll(reserverBtn, managerActions);

        card.getChildren().addAll(imagePane, nomLabel, typeLabel, capaLabel, chips, cardActions);
        return card;
    }

    private Label creerChip(String nomEquipement) {
        int qte = quantitesEquipements.getOrDefault(nomEquipement, 0);
        String texte = qte > 0 ? nomEquipement + " (" + qte + ")" : nomEquipement;
        Label chip = new Label(texte);
        chip.getStyleClass().add("room-chip");
        return chip;
    }

    // ---------------------------------------------------------------
    // Actions gestionnaire
    // ---------------------------------------------------------------
    private void ouvrirReservation(Salle salle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation-form.fxml"));
            Parent root = loader.load();
            ReservationFormController controller = loader.getController();
            controller.initData(currentUser, salle);

            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showPage(sallesFlow, "Erreur de navigation",
                    "Impossible d'ouvrir le formulaire de réservation.", currentUser, false);
        }
    }

    private void ouvrirModification(Salle salle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/salle-form.fxml"));
            Parent root = loader.load();
            SalleFormController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setSalle(salle); // pré-remplir le formulaire

            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showPage(sallesFlow, "Erreur de navigation",
                    "Impossible d'ouvrir le formulaire de modification.", currentUser, false);
        }
    }

    private void supprimerSalle(Salle salle) {
        boolean ok = salleService.supprimerSalle(salle.getIdSalle());
        if (ok) {
            NotificationUtil.showSuccess(
                    (Stage) sallesFlow.getScene().getWindow(),
                    "Salle supprimée",
                    "La salle '" + salle.getNom() + "' a été supprimée avec succès.",
                    currentUser);
        } else {
            NotificationUtil.showError(
                    (Stage) sallesFlow.getScene().getWindow(),
                    "Erreur de suppression",
                    "Impossible de supprimer la salle. Elle possède peut-être des réservations actives.",
                    currentUser);
        }
    }

    // ---------------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------------
    @FXML
    private void handleGoDashboard() {
        naviguer("/fxml/manager-dashboard.fxml", ctrl -> {
            ManagerDashboardController c = (ManagerDashboardController) ctrl;
            c.setCurrentUser(currentUser);
        });
    }

    @FXML
    private void handleMesReservations() {
        naviguer("/fxml/reservations-list.fxml", ctrl -> {
            ReservationsListController c = (ReservationsListController) ctrl;
            c.initData(currentUser);
        });
    }

    @FXML
    private void handleGoToAddSalle() {
        naviguer("/fxml/salle-form.fxml", ctrl -> {
            SalleFormController c = (SalleFormController) ctrl;
            c.setCurrentUser(currentUser);
        });
    }

    @FXML
    private void handleProfile() {
        naviguer("/fxml/profil.fxml", ctrl -> {
            ProfilController c = (ProfilController) ctrl;
            c.setCurrentUser(currentUser);
        });
    }

    @FXML
    private void handleLogout() {
        naviguer("/fxml/login-view.fxml", ctrl -> {
        });
    }

    // ---------------------------------------------------------------
    // Utilitaire de navigation
    // ---------------------------------------------------------------
    @FunctionalInterface
    interface ControllerInitializer {
        void init(Object controller);
    }

    private void naviguer(String fxmlPath, ControllerInitializer initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            initializer.init(loader.getController());
            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
