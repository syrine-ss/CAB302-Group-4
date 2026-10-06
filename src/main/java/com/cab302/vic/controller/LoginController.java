package com.cab302.vic.controller;

import com.cab302.vic.dao.SqliteUserDAO;
import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.model.User;
import com.cab302.vic.service.AuthException;
import com.cab302.vic.service.AuthService;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the login view.
 * Handles credential validation, session setup, and routing to the correct
 * dashboard based on the user's role.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService =
            new AuthService(new SqliteUserDAO(DatabaseManager.getInstance()));

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            User user = authService.login(username.trim(), password);
            SessionManager.getInstance().setCurrentUser(user);
            routeToDashboard(user);
        } catch (AuthException e) {
            showError(e.getMessage());
        } catch (IOException e) {
            showError("Could not open the dashboard: " + e.getMessage());
        }
    }

    @FXML
    protected void onRegisterLinkClick() {
        try {
            SceneNavigator.switchTo(usernameField, "register-view.fxml", "Create Account");
        } catch (IOException e) {
            showError("Could not open the registration screen.");
        }
    }

    private void routeToDashboard(User user) throws IOException {
        String fxml = user.getRole() == User.Role.COORDINATOR
                ? "coordinator-dashboard.fxml"
                : "volunteer-dashboard.fxml";
        String title = user.getRole() == User.Role.COORDINATOR
                ? "Coordinator Dashboard"
                : "Volunteer Dashboard";
        SceneNavigator.switchTo(usernameField, fxml, title);
    }

    private void showError(String message) {
        messageLabel.getStyleClass().removeAll("success");
        if (!messageLabel.getStyleClass().contains("error")) {
            messageLabel.getStyleClass().add("error");
        }
        messageLabel.setText(message);
    }
}
