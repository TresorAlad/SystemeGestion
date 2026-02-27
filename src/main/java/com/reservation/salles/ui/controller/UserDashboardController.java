package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Salle;
import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.model.Equipement;
import com.reservation.salles.service.SalleService;
import com.reservation.salles.service.EquipementService;
import com.reservation.salles.util.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur principal pour le tableau de bord utilisateur (Vue Demandeur).
 * Affiche la liste des salles disponibles, permet la recherche et lance le
 * processus de réservation.
 */
public class UserDashboardController {

    @FXML
    private Label currentUserLabel;
    @FXML
    private Label totalSallesLabel;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane sallesFlow;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnHistorique;

    private final SalleService salleService = new SalleService();
    private final EquipementService equipementService = new EquipementService();
    private Utilisateur currentUser;
    private final ObservableList<Salle> toutesLesSalles = FXCollections.observableArrayList();
    private Map<String, Integer> quantitesEquipements = new HashMap<>();

    /**
     * Initialise les données de l'utilisateur connecté et charge les salles.
     */
    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        currentUserLabel.setText(utilisateur.getNom());
        chargerSalles();
        rafraichirCartes(toutesLesSalles);
    }

    @FXML
    private void initialize() {
        // Mise en place de l'écouteur pour la barre de recherche
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> appliquerFiltre(newV));
        }
    }

    /**
     * Récupère la liste des salles depuis le service.
     */
    private void chargerSalles() {
        toutesLesSalles.setAll(salleService.listerToutesLesSalles());
        quantitesEquipements.clear();
        for (Equipement e : equipementService.listerEquipements()) {
            quantitesEquipements.put(e.getNom(), e.getQuantite());
        }
        if (totalSallesLabel != null) {
            totalSallesLabel.setText("Toutes mes salles (" + toutesLesSalles.size() + ")");
        }
    }

    /**
     * Filtre les salles affichées en fonction du texte saisi dans la barre de
     * recherche.
     */
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

    /**
     * Met à jour l'affichage des cartes de salles dans le FlowPane.
     */
    private void rafraichirCartes(List<Salle> salles) {
        if (sallesFlow == null)
            return;
        sallesFlow.getChildren().clear();
        for (Salle salle : salles) {
            sallesFlow.getChildren().add(creerCarteSalle(salle));
        }
    }

    /**
     * Crée graphiquement une "carte" (VBox) pour représenter une salle.
     */
    private VBox creerCarteSalle(Salle salle) {
        VBox card = new VBox(8);
        card.getStyleClass().add("room-card");

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

        // Gestion de l'affichage du statut Occupation/Disponibilité
        boolean occupee = salleService.estOccupeeMaintenant(salle.getIdSalle());
        String texteStatut = !salle.isDisponible() ? "Indisponible" : (occupee ? "Occupé" : "Disponible");
        String cssStatut = !salle.isDisponible() ? "availability-badge-off"
                : (occupee ? "availability-badge-occupied" : "availability-badge-on");

        Label badge = new Label(texteStatut);
        badge.getStyleClass().addAll("availability-badge", cssStatut);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(8, 8, 0, 0));

        imagePane.getChildren().addAll(imageView, badge);

        Label nomLabel = new Label(salle.getNom());
        nomLabel.getStyleClass().add("room-name");

        Label typeLabel = new Label(salle.getType());
        typeLabel.getStyleClass().add("room-meta");

        Label capaLabel = new Label(salle.getCapacite() + " personnes");
        capaLabel.getStyleClass().add("room-meta");

        HBox chips = new HBox(6);
        chips.setAlignment(Pos.CENTER);

        // On récupère les équipements de la salle (limité à 4 pour le design)
        List<Equipement> equips = salle.getEquipements();
        for (int i = 0; i < Math.min(equips.size(), 4); i++) {
            Equipement eq = equips.get(i);
            chips.getChildren().add(creerChipEquipement(eq));
        }

        Button reserverBtn = new Button("Réserver");
        reserverBtn.getStyleClass().add("primary-button");
        reserverBtn.setMaxWidth(Double.MAX_VALUE);
        reserverBtn.setOnAction(e -> ouvrirReservation(salle));

        card.getChildren().addAll(imagePane, nomLabel, typeLabel, capaLabel, chips, reserverBtn);
        return card;
    }

    /**
     * Crée un badge ("chip") visuel pour un équipement donné.
     */
    private Label creerChipEquipement(Equipement eq) {
        String texte = eq.getNom() + " (" + eq.getQuantite() + ")";
        Label chip = new Label(texte);
        chip.getStyleClass().add("room-chip");
        return chip;
    }

    /**
     * Ouvre l'écran du formulaire de réservation pour la salle sélectionnée.
     */
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
            NotificationUtil.showPage(sallesFlow, "Erreur de formulaire",
                    "Impossible d'ouvrir le formulaire de réservation.", currentUser, false);
        }
    }

    /**
     * Redirige vers la page d'historique des réservations.
     */
    @FXML
    private void handleMesReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historique.fxml"));
            Parent root = loader.load();
            HistoriqueController controller = loader.getController();
            if (controller == null) {
                NotificationUtil.showPage(sallesFlow, "Erreur de chargement",
                        "Désolé, une erreur est survenue lors de l'accès à l'historique.", currentUser, false);
                return;
            }
            controller.initData(currentUser);

            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            NotificationUtil.showPage(sallesFlow, "Erreur système",
                    "Impossible d'accéder à vos réservations pour le moment.", currentUser, false);
        }
    }

    /**
     * Redirige vers la page de profil.
     */
    @FXML
    private void handleProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profil.fxml"));
            Parent root = loader.load();
            ProfilController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Déconnecte l'utilisateur actuel.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToHistorique(ActionEvent event) throws IOException {
        handleMesReservations();
    }

    @FXML
    private void goToDashboard(ActionEvent event) throws IOException {
        if (event == null)
            return;
        Node source = (Node) event.getSource();
        if (source == null || source.getScene() == null)
            return;

        Stage stage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-dashboard.fxml"));
        Parent root = loader.load();
        UserDashboardController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
    }
}
