package com.cab302.vic.controller;

import com.cab302.vic.model.User;
import com.cab302.vic.service.AuthException;
import com.cab302.vic.service.AuthService;
import com.cab302.vic.service.RegistrationValidator;
import com.cab302.vic.service.RegistrationValidator.Field;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Controller for the account creation view.
 * Checks the whole form with {@link RegistrationValidator} so every problem is
 * shown next to its field, then delegates to {@link AuthService} for
 * persistence and routes to the login screen with the username filled in.
 */
public class RegisterController {

    private static final String INVALID = "invalid";

    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Label roleLabel;

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ChoiceBox<User.Role> roleChoice;

    @FXML private Label usernameError;
    @FXML private Label fullNameError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;
    @FXML private Label passwordHint;
    @FXML private Label messageLabel;

    private final AuthService authService = ServiceFactory.getInstance().auth();

    private final Map<Field, TextInputControl> inputs = new EnumMap<>(Field.class);
    private final Map<Field, Label> errorLabels = new EnumMap<>(Field.class);

    /**
     * Sets up the role choices, links labels to their fields, and starts the
     * live password checklist.
     */
    @FXML
    public void initialize() {
        roleChoice.getItems().setAll(User.Role.VOLUNTEER, User.Role.COORDINATOR);
        roleChoice.setValue(User.Role.VOLUNTEER);

        usernameLabel.setLabelFor(usernameField);
        fullNameLabel.setLabelFor(fullNameField);
        emailLabel.setLabelFor(emailField);
        passwordLabel.setLabelFor(passwordField);
        confirmPasswordLabel.setLabelFor(confirmPasswordField);
        roleLabel.setLabelFor(roleChoice);

        inputs.put(Field.USERNAME, usernameField);
        inputs.put(Field.FULL_NAME, fullNameField);
        inputs.put(Field.EMAIL, emailField);
        inputs.put(Field.PASSWORD, passwordField);
        inputs.put(Field.CONFIRM_PASSWORD, confirmPasswordField);
        errorLabels.put(Field.USERNAME, usernameError);
        errorLabels.put(Field.FULL_NAME, fullNameError);
        errorLabels.put(Field.EMAIL, emailError);
        errorLabels.put(Field.PASSWORD, passwordError);
        errorLabels.put(Field.CONFIRM_PASSWORD, confirmPasswordError);

        // Clear a field's error as soon as the user starts fixing it
        inputs.forEach((field, input) ->
                input.textProperty().addListener((obs, old, now) -> clearFieldError(field)));

        passwordHint.setText(RegistrationValidator.passwordChecklist(""));
        passwordField.textProperty().addListener((obs, old, now) ->
                passwordHint.setText(RegistrationValidator.passwordChecklist(now)));
    }

    @FXML
    protected void onCreateAccountClick() {
        String username = trim(usernameField.getText());
        String fullName = trim(fullNameField.getText());
        String email = trim(emailField.getText());
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        User.Role role = roleChoice.getValue();

        hideMessage();
        errorLabels.keySet().forEach(this::clearFieldError);

        Map<Field, String> errors = RegistrationValidator.validate(username, fullName, email, password, confirm);
        if (!errors.isEmpty()) {
            errors.forEach(this::showFieldError);
            inputs.get(errors.keySet().iterator().next()).requestFocus();
            return;
        }

        try {
            authService.register(username, password, fullName, email, role);
        } catch (AuthException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("taken")) {
                showFieldError(Field.USERNAME, e.getMessage() + ". Try a different one.");
                usernameField.requestFocus();
            } else {
                showError(e.getMessage());
            }
            return;
        }

        try {
            LoginController login = SceneNavigator.switchTo(
                    usernameField, "login-view.fxml", "Volunteer Impact Coordinator");
            login.prefillAfterRegistration(username);
        } catch (IOException e) {
            showError("Account created, but the login screen could not be opened: " + e.getMessage());
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

    private void showFieldError(Field field, String message) {
        TextInputControl input = inputs.get(field);
        if (!input.getStyleClass().contains(INVALID)) {
            input.getStyleClass().add(INVALID);
        }
        Label label = errorLabels.get(field);
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void clearFieldError(Field field) {
        inputs.get(field).getStyleClass().removeAll(INVALID);
        Label label = errorLabels.get(field);
        label.setText("");
        label.setVisible(false);
        label.setManaged(false);
    }

    private void showError(String message) {
        messageLabel.getStyleClass().removeAll("success");
        if (!messageLabel.getStyleClass().contains("error")) {
            messageLabel.getStyleClass().add("error");
        }
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void hideMessage() {
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }
}
