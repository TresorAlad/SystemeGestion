package com.reservation.salles.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationController {

    @FXML
    private Label iconLabel;
    @FXML
    private Label messageLabel;

    public void initSuccess(String message) {
        iconLabel.setText("✓");
        iconLabel.setStyle("-fx-text-fill: #005b66; -fx-font-size: 48px;");
        messageLabel.setText(message);
    }

    public void initError(String message) {
        iconLabel.setText("✕");
        iconLabel.setStyle("-fx-text-fill: #f56565; -fx-font-size: 48px;");
        messageLabel.setText(message);
    }

    public void close() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }
}

