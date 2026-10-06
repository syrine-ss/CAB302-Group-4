package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.User;
import com.cab302.vic.service.EventService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.service.SignupException;
import com.cab302.vic.service.SignupService;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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

/**
 * Dashboard shown to volunteers after login.
 *
 * <p>Lists upcoming events and lets the volunteer sign up or withdraw
 * directly from the list, so the whole flow takes one click.
 */
public class VolunteerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label hoursSummaryLabel;
    @FXML private ListView<Event> eventList;

    private final EventService eventService = ServiceFactory.getInstance().events();
    private final SignupService signupService = ServiceFactory.getInstance().signups();

    /**
     * Initialises the welcome message and events for the (volunteer) user on
     * their dashboard after logging in.
     */
    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName());
        eventList.setCellFactory(list -> new EventCell(this));
        refresh();
    }

    /** Reload events and the hours summary from the database. */
    void refresh() {
        eventList.setItems(FXCollections.observableArrayList(eventService.findAll()));
        double approved = ServiceFactory.getInstance().hours()
                .totalApprovedHoursForVolunteer(currentUserId());
        hoursSummaryLabel.setText(String.format("%.1f approved hours", approved));
    }

    /** Called by the cell's button. Signs up, or withdraws when already signed up. */
    void toggleSignup(Event event) {
        int me = currentUserId();
        try {
            if (signupService.isSignedUp(event.getId(), me)) {
                signupService.withdraw(event.getId(), me);
                info("You have withdrawn from " + event.getTitle() + ".");
            } else {
                signupService.signUp(event.getId(), me);
                info("You are signed up to " + event.getTitle() + ".");
            }
            refresh();
        } catch (SignupException e) {
            error(e.getMessage());
        }
    }

    @FXML
    protected void onMyHoursClick() {
        try {
            SceneNavigator.switchTo(welcomeLabel, "volunteer-hours.fxml", "My Hours");
        } catch (IOException e) {
            error("Could not open your hours: " + e.getMessage());
        }
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
                    SceneNavigator.switchTo(welcomeLabel, "login-view.fxml",
                            "Volunteer Impact Coordinator");
                } catch (IOException e) {
                    error("Could not return to login: " + e.getMessage());
                }
            }
        });
    }

    boolean isSignedUp(Event event) {
        return signupService.isSignedUp(event.getId(), currentUserId());
    }

    int spotsRemaining(Event event) {
        return signupService.spotsRemaining(event);
    }

    private static int currentUserId() {
        return SessionManager.getInstance().getCurrentUser().getId();
    }

    private void info(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    /** Renders one event as a card with a sign up / withdraw action. */
    private static class EventCell extends ListCell<Event> {

        private final VolunteerDashboardController parent;

        EventCell(VolunteerDashboardController parent) {
            this.parent = parent;
        }

        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label title = new Label(event.getTitle());
            title.getStyleClass().add("event-title");

            Label meta = new Label(event.getEventDate()
                    + (event.getEventTime().isBlank() ? "" : " at " + event.getEventTime())
                    + "  |  " + safe(event.getLocation()));
            meta.getStyleClass().add("event-meta");

            Label desc = new Label(safe(event.getDescription()));
            desc.setWrapText(true);
            desc.getStyleClass().add("event-meta");

            boolean signedUp = parent.isSignedUp(event);
            int left = parent.spotsRemaining(event);

            Label spots = new Label(signedUp
                    ? "You are signed up"
                    : (left == 0 ? "Full" : left + " of " + event.getVolunteersNeeded() + " places left"));
            spots.getStyleClass().add(signedUp ? "success" : "event-meta");

            Button action = new Button(signedUp ? "Withdraw" : "Sign up");
            action.getStyleClass().add(signedUp ? "secondary" : "primary");
            action.setDisable(!signedUp && left == 0);
            action.setOnAction(e -> parent.toggleSignup(event));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox footer = new HBox(10, spots, spacer, action);

            VBox box = new VBox(4, title, meta, desc, footer);
            box.getStyleClass().add("event-card");

            setGraphic(box);
            setText(null);
        }

        private static String safe(String s) {
            return s == null || s.isBlank() ? "" : s;
        }
    }
}
