package com.cab302.vic.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        // TODO: authenticate against the users table (Sprint 1)
        messageLabel.setText("Login logic not yet implemented.");
    }

    @FXML
    protected void onRegisterLinkClick() {
        // TODO: switch to registration view (Sprint 1)
        messageLabel.setText("Registration screen coming soon.");
    }
}
