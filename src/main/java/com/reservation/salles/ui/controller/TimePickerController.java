package com.reservation.salles.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import java.time.LocalTime;
import javafx.util.StringConverter;

/**
 * Contrôleur pour le sélecteur d'heure personnalisé (TimePicker).
 * Utilisé comme popup pour faciliter la saisie des horaires sans erreur de
 * format.
 */
public class TimePickerController {

    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;

    private Popup popup;
    private TextField targetField;

    @FXML
    public void initialize() {
        // Formatteur pour afficher toujours 2 chiffres (01, 02, etc.)
        StringConverter<Integer> twoDigitConverter = new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "00" : String.format("%02d", value);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return string == null || string.isEmpty() ? 0 : Integer.parseInt(string.trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        };
        hourSpinner.getValueFactory().setConverter(twoDigitConverter);
        minuteSpinner.getValueFactory().setConverter(twoDigitConverter);
    }

    /**
     * Initialise le popup avec le champ de texte cible.
     * Tente de pré-remplir les spinners si une heure est déjà présente.
     */
    public void init(Popup popup, TextField targetField) {
        this.popup = popup;
        this.targetField = targetField;

        try {
            String text = targetField.getText().replace(" ", "");
            if (!text.isEmpty() && text.contains(":")) {
                LocalTime lt = LocalTime.parse(text);
                hourSpinner.getValueFactory().setValue(lt.getHour());
                minuteSpinner.getValueFactory().setValue(lt.getMinute());
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void handleCancel() {
        popup.hide();
    }

    /**
     * Vide le champ de texte cible.
     */
    @FXML
    private void handleClear() {
        targetField.setText("");
        popup.hide();
    }

    /**
     * Envoie l'heure sélectionnée au format "HH : mm" vers le champ cible.
     */
    @FXML
    private void handleSet() {
        int h = hourSpinner.getValue();
        int m = minuteSpinner.getValue();
        targetField.setText(String.format("%02d : %02d", h, m));
        popup.hide();
    }
}
