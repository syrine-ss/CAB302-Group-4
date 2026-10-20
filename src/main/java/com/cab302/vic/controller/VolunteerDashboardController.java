package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;
import com.cab302.vic.model.User;
import com.cab302.vic.service.*;
import com.cab302.vic.util.EventDisplay;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Dashboard shown to volunteers after login.
 * Lists upcoming events first and past events underneath, shows how many
 * spots are left, and lets the volunteer sign up for or withdraw from each
 * one. The rules themselves live in {@link SignupService}; this screen only
 * decides which button to show and reports the outcome.
 */
public class VolunteerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private CheckBox myEventsOnly;
    @FXML private Label statusLabel;
    @FXML private VBox eventsBox;

    private final EventService eventService = ServiceFactory.getInstance().events();
    private final SignupService signupService = ServiceFactory.getInstance().signups();
    private final HoursService hoursService = ServiceFactory.getInstance().hours();

    private User user;

    /** Set once when the scene is edited. Lets the controller know which event to load */
    public static final SimpleObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();

    /**
     * Initialises the welcome message and events for the (volunteer) user on
     * their dashboard after logging in.
     */
    @FXML
    public void initialize() {
        user = SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName());
        refreshEvents();
    }

    @FXML
    protected void onFilterChanged() {
        hideStatus();
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

    @FXML
    protected void onLogHoursClick(Event event) {
        selectedEvent.set(event);
        try {
            SceneNavigator.switchTo(welcomeLabel, "log-hours.fxml", "Log hours");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not open log hours form: " + e.getMessage()).showAndWait();
        }
    }

    /** Rebuilds the event cards so spot counts and buttons reflect the latest signups. */
    private void refreshEvents() {
        List<Event> events = eventService.findAll();
        if (myEventsOnly.isSelected()) {
            events = events.stream()
                    .filter(e -> signupService.isSignedUp(e.getId(), user.getId()))
                    .toList();
        }

        eventsBox.getChildren().clear();
        if (events.isEmpty()) {
            // Explain an empty screen instead of leaving it blank (finding V6)
            eventsBox.getChildren().add(emptyState(myEventsOnly.isSelected()
                    ? "You haven't signed up for any events yet. Untick the box above to see all events."
                    : "No events have been posted yet. Check back soon."));
            return;
        }

        List<Event> upcoming = EventDisplay.upcoming(events, signupService::hasPassed);
        List<Event> past = EventDisplay.past(events, signupService::hasPassed);

        eventsBox.getChildren().add(sectionHeading("Upcoming (" + upcoming.size() + ")"));
        if (upcoming.isEmpty()) {
            eventsBox.getChildren().add(emptyState("No upcoming events right now."));
        }
        upcoming.forEach(e -> eventsBox.getChildren().add(card(e)));

        if (!past.isEmpty()) {
            eventsBox.getChildren().add(sectionHeading("Past (" + past.size() + ")"));
            past.forEach(e -> eventsBox.getChildren().add(card(e)));
        }
    }

    private void signUp(Event event) {
        try {
            signupService.signUp(event.getId(), user.getId());
            refreshEvents();
            showStatus("You're signed up for " + event.getTitle() + " on "
                    + EventDisplay.when(event) + ".", false);
        } catch (SignupException e) {
            showStatus(e.getMessage(), true);
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
            showStatus("You've withdrawn from " + event.getTitle() + ".", false);
        } catch (SignupException e) {
            showStatus(e.getMessage(), true);
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.getStyleClass().removeAll("success", "error");
        statusLabel.getStyleClass().add(isError ? "error" : "success");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    private static Label sectionHeading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-heading");
        return label;
    }

    private static Label emptyState(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("subtitle");
        return label;
    }

    /**
     * Draws one event as a card, with its spots left and the action that fits
     * the volunteer's situation: sign up, withdraw, or a status if neither applies.
     */
    private HBox card(Event event) {
        VBox details = new VBox(4);
        Label title = new Label(event.getTitle());
        title.getStyleClass().add("list-title");
        String location = safe(event.getLocation());
        Label when = new Label(location.isEmpty()
                ? EventDisplay.when(event)
                : EventDisplay.when(event) + "  \u00b7  " + location);
        when.getStyleClass().add("event-meta");
        details.getChildren().addAll(title, when);
        String description = safe(event.getDescription());
        if (!description.isEmpty()) {
            Label desc = new Label(description);
            desc.setWrapText(true);
            desc.getStyleClass().add("event-meta");
            details.getChildren().add(desc);
        }
        Label spots = new Label(signupService.spotsLeft(event) + " of "
                + event.getVolunteersNeeded() + " spots left");
        spots.getStyleClass().add("event-meta");
        details.getChildren().add(spots);
        details.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(details, Priority.ALWAYS);

        Node logHoursButton = logHoursActionFor(event);

        HBox card = new HBox(12, details, actionFor(event));

        if (logHoursButton != null) {
            card.getChildren().add(logHoursButton);
        }

        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("list-card");
        return card;
    }

    private Node actionFor(Event event) {
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
            Label signedUp = new Label("\u2713 You're signed up");
            signedUp.getStyleClass().add("signed-up");
            Button withdraw = new Button("Withdraw");
            withdraw.getStyleClass().add("secondary");
            withdraw.setAccessibleText("Withdraw from " + event.getTitle());
            withdraw.setOnAction(e -> withdraw(event));
            VBox box = new VBox(6, signedUp, withdraw);
            box.setAlignment(Pos.CENTER_RIGHT);
            return box;
        }
        if (passed) {
            return status("Event has passed");
        }
        if (signupService.spotsLeft(event) <= 0) {
            return status("Full");
        }
        Button signUp = new Button("Sign up");
        signUp.getStyleClass().add("primary");
        signUp.setAccessibleText("Sign up for " + event.getTitle());
        signUp.setOnAction(e -> signUp(event));
        return signUp;
    }

    private Node logHoursActionFor(Event event) {

        if (!signupService.hasPassed(event)) {
            return null;
        }

        Optional<Signup> signup = signupService.signupsForEvent(event.getId()).stream()
                .filter(s -> s.getUserId() == user.getId())
                .findFirst();

        if (signup.isEmpty() || !signupService.signupAttended(signup.get(), event)) {
            return null;
        }

        Optional<HoursEntry> latestEntry = hoursService.latestHoursForEvent(
                event.getId(),
                user.getId()
        );

        if (latestEntry.isPresent() && latestEntry.get().getStatus() == HoursEntry.Status.APPROVED) {
            return null;
        }

        Button logHours = new Button("Log hours");
        logHours.getStyleClass().add("primary");
        logHours.setAccessibleText("Log hours for " + event.getTitle());
        logHours.setOnAction(e -> onLogHoursClick(event));

        return logHours;
    }


    private static Label status(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("event-meta");
        return label;
    }

    private static String safe(String s) {
        return s == null || s.isBlank() ? "" : s.trim();
    }
}
