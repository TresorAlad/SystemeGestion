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

    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        currentUserLabel.setText(utilisateur.getNom());
        chargerSalles();
        rafraichirCartes(toutesLesSalles);
    }

    @FXML
    private void initialize() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> appliquerFiltre(newV));
        }
    }

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

        Label nomLabel = new Label(salle.getNom());
        nomLabel.getStyleClass().add("room-name");

        Label typeLabel = new Label(salle.getType());
        typeLabel.getStyleClass().add("room-meta");

        Label capaLabel = new Label(salle.getCapacite() + " personnes");
        capaLabel.getStyleClass().add("room-meta");

        HBox chips = new HBox(6);
        chips.getChildren().addAll(
                creerChipAvecQuantite("Projecteur"),
                creerChipAvecQuantite("WiFi"),
                creerChipAvecQuantite("PC"));

        Button reserverBtn = new Button("Réserver");
        reserverBtn.getStyleClass().add("primary-button");
        reserverBtn.setMaxWidth(Double.MAX_VALUE);
        reserverBtn.setOnAction(e -> ouvrirReservation(salle));

        card.getChildren().addAll(imagePane, nomLabel, typeLabel, capaLabel, chips, reserverBtn);
        return card;
    }

    private Label creerChipAvecQuantite(String nomEquipement) {
        int qte = quantitesEquipements.getOrDefault(nomEquipement, 0);
        String texte = qte > 0 ? nomEquipement + " (" + qte + ")" : nomEquipement;
        Label chip = new Label(texte);
        chip.getStyleClass().add("room-chip");
        return chip;
    }

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
            NotificationUtil.erreur("Impossible d'ouvrir le formulaire de réservation.");
        }
    }

    @FXML
    private void handleMesReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/historique.fxml"));
            Parent root = loader.load();
            HistoriqueController controller = loader.getController();
            if (controller == null) {
                NotificationUtil.erreur("Erreur: Le contrôleur est null.");
                return;
            }
            controller.initData(currentUser);

            Stage stage = (Stage) sallesFlow.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            String errMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            System.err.println("ERREUR DÉTAILLÉE: " + errMsg);
            // On affiche l'erreur complète pour aider au débogage
            NotificationUtil.erreur("Impossible d'ouvrir la liste des réservations: " + errMsg
                    + "\nConsultez la console pour plus de détails.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("EXCEPTION GENERALE: " + e.getClass().getName() + ": " + e.getMessage());
            NotificationUtil.erreur("Erreur inattendue: " + e.getMessage());
        }
    }

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
        if (event == null) {
            return;
        }
        Node source = (Node) event.getSource();
        if (source == null || source.getScene() == null) {
            return;
        }
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user-dashboard.fxml"));
        Parent root = loader.load();
        UserDashboardController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
    }
}
