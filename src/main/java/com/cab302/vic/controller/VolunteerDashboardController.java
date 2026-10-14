package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;
import com.cab302.vic.model.User;
import com.cab302.vic.service.EventService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.service.SignupException;
import com.cab302.vic.service.SignupService;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

/**
 * Dashboard shown to volunteers after login.
 * Lists all events, shows how many spots are left, and lets the volunteer
 * sign up for or withdraw from each one. The rules themselves live in
 * {@link SignupService}; this screen only decides which button to show and
 * reports any error the service raises.
 */
public class VolunteerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<Event> eventList;

    private final EventService eventService = ServiceFactory.getInstance().events();
    private final SignupService signupService = ServiceFactory.getInstance().signups();

    private User user;

    /**
     * Initialises the welcome message and events for the (volunteer) user on
     * their dashboard after logging in.
     */
    @FXML
    public void initialize() {
        user = SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName());
        eventList.setCellFactory(list -> new EventCell());
        refreshEvents();
    }

    @FXML
    protected void onLogoutClick() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to log out?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                SessionManager.getInstance().clear();
                try {
                    SceneNavigator.switchTo(welcomeLabel, "login-view.fxml", "Volunteer Impact Coordinator");
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Could not return to login: " + e.getMessage()).showAndWait();
                }
            }
        });
    }

    /** Reloads the events so spot counts and buttons reflect the latest signups. */
    private void refreshEvents() {
        eventList.setItems(FXCollections.observableArrayList(eventService.findAll()));
    }

    private void signUp(Event event) {
        try {
            signupService.signUp(event.getId(), user.getId());
            refreshEvents();
        } catch (SignupException e) {
            showError(e.getMessage());
        }
    }

    private void withdraw(Event event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Withdraw from \"" + event.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isEmpty() || response.get() != ButtonType.YES) {
            return;
        }
        try {
            signupService.withdraw(event.getId(), user.getId());
            refreshEvents();
        } catch (SignupException e) {
            showError(e.getMessage());
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Draws one event as a card, with its spots left and the action that fits
     * the volunteer's situation: sign up, withdraw, or a status if neither applies.
     */
    private class EventCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            VBox details = new VBox(4);
            Label title = new Label(event.getTitle());
            title.getStyleClass().add("event-title");
            Label meta = new Label(event.getEventDate() + " at " + safe(event.getLocation()));
            meta.getStyleClass().add("event-meta");
            Label desc = new Label(safe(event.getDescription()));
            desc.setWrapText(true);
            desc.getStyleClass().add("event-meta");
            Label spots = new Label(signupService.spotsLeft(event) + " of "
                    + event.getVolunteersNeeded() + " spots left");
            spots.getStyleClass().add("event-meta");
            details.getChildren().addAll(title, meta, desc, spots);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox card = new HBox(12, details, spacer, actionFor(event));
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("event-card");

            setGraphic(card);
            setText(null);
        }

        private javafx.scene.Node actionFor(Event event) {
            Optional<Signup> signup = signupService.signupsForEvent(event.getId()).stream()
                    .filter(s -> s.getUserId() == user.getId())
                    .findFirst();
            boolean passed = signupService.hasPassed(event);

            if (signup.isPresent()) {
                if (signup.get().isAttended()) {
                    return status("Attended");
                }
                if (passed) {
                    return status("Signed up");
                }
                Button withdraw = new Button("Withdraw");
                withdraw.getStyleClass().add("secondary");
                withdraw.setOnAction(e -> withdraw(event));
                return withdraw;
            }
            if (passed) {
                return status("Event has passed");
            }
            if (signupService.spotsLeft(event) <= 0) {
                return status("Full");
            }
            Button signUp = new Button("Sign up");
            signUp.getStyleClass().add("primary");
            signUp.setOnAction(e -> signUp(event));
            return signUp;
        }

        private Label status(String text) {
            Label label = new Label(text);
            label.getStyleClass().add("event-meta");
            return label;
        }

        private String safe(String s) {
            return s == null || s.isBlank() ? "" : s;
        }
    }
}
