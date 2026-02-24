package com.reservation.salles.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import java.time.LocalTime;
import javafx.util.StringConverter;

public class TimePickerController {

    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;

    private Popup popup;
    private TextField targetField;

    @FXML
    public void initialize() {
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

    public void init(Popup popup, TextField targetField) {
        this.popup = popup;
        this.targetField = targetField;

        // Tenter d'extraire l'heure actuelle du champ cible
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

    @FXML
    private void handleClear() {
        targetField.setText("");
        popup.hide();
    }

    @FXML
    private void handleSet() {
        int h = hourSpinner.getValue();
        int m = minuteSpinner.getValue();
        targetField.setText(String.format("%02d : %02d", h, m));
        popup.hide();
    }
}
