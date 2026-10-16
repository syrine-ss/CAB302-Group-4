package com.cab302.vic.controller;

import com.cab302.vic.model.User;
import com.cab302.vic.service.AuthException;
import com.cab302.vic.service.AuthService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.io.IOException;

/**
 * Controller for the login view.
 * Handles credential validation, session setup, and routing to the correct
 * dashboard based on the user's role.
 */
public class LoginController {

    private static final String INVALID = "invalid";

    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = ServiceFactory.getInstance().auth();

    /**
     * Links each visible label to its field so screen readers announce the
     * label and Alt + the underlined letter moves focus to the field.
     */
    @FXML
    public void initialize() {
        usernameLabel.setLabelFor(usernameField);
        passwordLabel.setLabelFor(passwordField);
        usernameField.textProperty().addListener((obs, old, now) -> clearInvalid(usernameField));
        passwordField.textProperty().addListener((obs, old, now) -> clearInvalid(passwordField));
    }

    /**
     * Called by the register screen after an account is created, so the new
     * user lands here with their username filled in and only has to type
     * their password.
     *
     * @param username the username that was just registered
     */
    public void prefillAfterRegistration(String username) {
        usernameField.setText(username);
        showSuccess("Account created. Enter your password to log in.");
        Platform.runLater(passwordField::requestFocus);
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean missingUser = username == null || username.isBlank();
        boolean missingPassword = password == null || password.isBlank();
        if (missingUser || missingPassword) {
            if (missingUser) markInvalid(usernameField);
            if (missingPassword) markInvalid(passwordField);
            (missingUser ? usernameField : passwordField).requestFocus();
            showError("Please enter both username and password.");
            return;
        }

        try {
            User user = authService.login(username.trim(), password);
            SessionManager.getInstance().setCurrentUser(user);
            routeToDashboard(user);
        } catch (AuthException e) {
            passwordField.clear();
            passwordField.requestFocus();
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

    private static void markInvalid(TextInputControl field) {
        if (!field.getStyleClass().contains(INVALID)) {
            field.getStyleClass().add(INVALID);
        }
    }

    private static void clearInvalid(TextInputControl field) {
        field.getStyleClass().removeAll(INVALID);
    }

    private void showError(String message) {
        showMessage(message, "error", "success");
    }

    private void showSuccess(String message) {
        showMessage(message, "success", "error");
    }

    private void showMessage(String message, String addClass, String removeClass) {
        messageLabel.getStyleClass().removeAll(removeClass);
        if (!messageLabel.getStyleClass().contains(addClass)) {
            messageLabel.getStyleClass().add(addClass);
        }
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}
