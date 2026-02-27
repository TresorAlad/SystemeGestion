package com.reservation.salles.ui.controller;

import com.reservation.salles.model.Utilisateur;
import com.reservation.salles.service.UtilisateurService;
import com.reservation.salles.util.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion du profil utilisateur.
 * Permet de modifier le nom et le mot de passe, et pour les gestionnaires,
 * de promouvoir d'autres utilisateurs.
 */
public class ProfilController {

    @FXML
    private Label currentUserLabelTop;
    @FXML
    private TextField nomField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordVisibleField;
    @FXML
    private Label emailLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private VBox adminSection;
    @FXML
    private ComboBox<Utilisateur> userComboBox;

    private boolean isPasswordVisible = false;
    private Utilisateur currentUser;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    /**
     * Définit l'utilisateur dont on affiche le profil et charge les informations.
     */
    public void setCurrentUser(Utilisateur utilisateur) {
        this.currentUser = utilisateur;
        chargerInfos();
    }

    /**
     * Remplit les champs du formulaire avec les données de l'utilisateur actuel.
     * Affiche la section d'administration si l'utilisateur est gestionnaire.
     */
    private void chargerInfos() {
        if (currentUser == null)
            return;

        currentUserLabelTop.setText(currentUser.getNom());
        nomField.setText(currentUser.getNom());
        emailLabel.setText(currentUser.getEmail());
        roleLabel.setText(currentUser.getRole());
        passwordField.setText(currentUser.getMotDePasse());
        passwordVisibleField.setText(currentUser.getMotDePasse());

        if (currentUser.estGestionnaire()) {
            adminSection.setVisible(true);
            chargerListeUtilisateurs();
        }
    }

    /**
     * Charge les utilisateurs standards dans la ComboBox pour permettre leur
     * promotion.
     */
    private void chargerListeUtilisateurs() {
        List<Utilisateur> allUsers = utilisateurService.listerTousLesUtilisateurs();
        // Filtrer pour ne montrer que les UTILISATEURS simples
        List<Utilisateur> simpleUsers = allUsers.stream()
                .filter(u -> u.estUtilisateur())
                .collect(Collectors.toList());

        userComboBox.setItems(FXCollections.observableArrayList(simpleUsers));
        userComboBox.setConverter(new StringConverter<Utilisateur>() {
            @Override
            public String toString(Utilisateur u) {
                return u == null ? "" : u.getNom() + " (" + u.getEmail() + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Promeut l'utilisateur sélectionné au rang de Gestionnaire.
     */
    @FXML
    private void handlePromouvoir() {
        Utilisateur selected = userComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            NotificationUtil.showPage(nomField, "Sélection erronée",
                    "Veuillez sélectionner un utilisateur à promouvoir.", currentUser, false);
            return;
        }

        utilisateurService.promouvoirEnGestionnaire(selected.getIdUtilisateur());
        NotificationUtil.showSuccess(
                (Stage) nomField.getScene().getWindow(),
                "Promotion réussie",
                "L'utilisateur " + selected.getNom() + " a été promu Gestionnaire avec succès.",
                currentUser);
    }

    /**
     * Enregistre les modifications apportées au profil (nom, mot de passe).
     */
    @FXML
    private void handleSauvegarder() {
        String nouveauNom = nomField.getText();
        String nouveauPass = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();

        if (nouveauNom == null || nouveauNom.isBlank() || nouveauPass == null || nouveauPass.isBlank()) {
            NotificationUtil.showPage(nomField, "Données invalides", "Le nom et le mot de passe sont obligatoires.",
                    currentUser, false);
            return;
        }

        utilisateurService.mettreAJourProfil(currentUser.getIdUtilisateur(), nouveauNom, nouveauPass);

        // Mettre à jour l'objet local
        currentUser.setNom(nouveauNom);
        currentUser.setMotDePasse(nouveauPass);

        currentUserLabelTop.setText(nouveauNom);
        NotificationUtil.showSuccess(
                (Stage) nomField.getScene().getWindow(),
                "Profil mis à jour",
                "Vos informations ont été enregistrées avec succès.",
                currentUser);
    }

    @FXML
    private void handleRetour() {
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

            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bascule l'affichage du mot de passe entre masqué et visible.
     */
    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            isPasswordVisible = false;
        } else {
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            isPasswordVisible = true;
        }
    }
}
