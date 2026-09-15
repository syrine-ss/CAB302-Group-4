package com.cab302.vic.controller;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.dao.SqliteUserDAO;
import com.cab302.vic.model.User;
import com.cab302.vic.service.AuthException;
import com.cab302.vic.service.AuthService;
import com.cab302.vic.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the account creation view.
 * Delegates to {@link AuthService} for validation and persistence,
 * then routes back to the login screen with a success flag.
 */
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ChoiceBox<User.Role> roleChoice;
    @FXML private Label messageLabel;

    private final AuthService authService =
            new AuthService(new SqliteUserDAO(DatabaseManager.getInstance()));

    @FXML
    public void initialize() {
        roleChoice.getItems().setAll(User.Role.VOLUNTEER, User.Role.COORDINATOR);
        roleChoice.setValue(User.Role.VOLUNTEER);
    }

    @FXML
    protected void onCreateAccountClick() {
        String username = trim(usernameField.getText());
        String fullName = trim(fullNameField.getText());
        String email = trim(emailField.getText());
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        User.Role role = roleChoice.getValue();

        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            User user = authService.register(username, password, fullName, email, role);
            showSuccess("Account created for " + user.getUsername() + ". Please log in.");
            passwordField.clear();
            confirmPasswordField.clear();
        } catch (AuthException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    protected void onBackToLoginClick() {
        try {
            SceneNavigator.switchTo(usernameField, "login-view.fxml", "Volunteer Impact Coordinator");
        } catch (IOException e) {
            showError("Could not return to login: " + e.getMessage());
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void showError(String message) {
        messageLabel.getStyleClass().removeAll("success");
        if (!messageLabel.getStyleClass().contains("error")) {
            messageLabel.getStyleClass().add("error");
        }
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.getStyleClass().removeAll("error");
        if (!messageLabel.getStyleClass().contains("success")) {
            messageLabel.getStyleClass().add("success");
        }
        messageLabel.setText(message);
    }
}
