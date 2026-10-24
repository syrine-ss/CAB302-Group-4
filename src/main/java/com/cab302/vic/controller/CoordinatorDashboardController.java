package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.User;
import com.cab302.vic.service.EventException;
import com.cab302.vic.service.EventService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.service.SignupService;
import com.cab302.vic.util.EventDisplay;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard shown to coordinators after login.
 * Lists the events they've created and provides a button to add a new one.
 */
public class CoordinatorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<Event> eventList;

    private final EventService eventService = ServiceFactory.getInstance().events();
    private final SignupService signupService = ServiceFactory.getInstance().signups();

    /** Set once when the scene is edited. Lets a controller know which event to load */
    public static final SimpleObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();

    /**
     * Initialises the welcome message and events for the (coordinator) user on
     * their dashboard after logging in.
     */
    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName());
        eventList.setCellFactory(list -> new EventCell());
        refreshEvents();
    }

    private void refreshEvents() {
        User user = SessionManager.getInstance().getCurrentUser();
        List<Event> events = eventService.findByCoordinator(user.getId());
        events = events.stream()
                .sorted(Comparator.comparing(Event::getEventDate).reversed())
                .collect(Collectors.toList());

        eventList.setItems(FXCollections.observableArrayList(events));
    }

    /**
     * Helper method to handle opening the event form.
     */

    private void openEventForm(String windowTitle) {
        try {
            SceneNavigator.switchTo(welcomeLabel, "event-form.fxml", windowTitle);
        } catch (IOException e) {
            showError("Could not open the event form: " + e.getMessage());
        }
    }

    @FXML
    protected void onCreateEventClick() {
        selectedEvent.set(null);
        openEventForm("Create Event");
    }

    @FXML
    protected void onEditSelectedEventClick() {
        Event event = eventList.getSelectionModel().getSelectedItem();
        if (event == null) {
            showError("Select an event to edit first.");
            return;
        }
        selectedEvent.set(event);
        openEventForm("Edit Event");
    }

    @FXML
    protected void onReviewSelectedEventClick() {
        Event event = eventList.getSelectionModel().getSelectedItem();
        if (event == null) {
            showError("Select an event to review first.");
            return;
        }
        selectedEvent.set(event);
        try {
            SceneNavigator.switchTo(welcomeLabel, "event-review.fxml", "Review event");
        } catch (IOException e) {
            showError("Could not open the event form: " + e.getMessage());
        }
    }

    @FXML
    protected void onCancelEventClick() {
        Event event = eventList.getSelectionModel().getSelectedItem();

        if (event == null) {
            showError("Select an event to cancel first.");
            return;
        }

        if (signupService.hasPassed(event)) {
            new Alert(Alert.AlertType.ERROR, "Can not delete events that have passed.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this event?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    eventService.cancelEvent(event.getId());
                    refreshEvents();
                } catch (EventException e) {
                    showError("Could not cancel event: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    protected void onProfileClick() {
        try {
            SceneNavigator.switchTo(welcomeLabel, "profile.fxml", "Profile");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not open profile: " + e.getMessage()).showAndWait();
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
                    SceneNavigator.switchTo(welcomeLabel, "login-view.fxml", "Volunteer Impact Coordinator");
                } catch (IOException e) {
                    showError("Could not return to login: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    // Custom cell rendering an event card
    private static class EventCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            VBox box = new VBox(4);
            box.getStyleClass().add("list-card");
            Label title = new Label(event.getTitle());
            title.getStyleClass().add("list-title");
            Label meta = new Label(EventDisplay.when(event) + " at " + safe(event.getLocation())
                    + "  |  " + event.getVolunteersNeeded() + " volunteers needed");
            meta.getStyleClass().add("event-meta");
            box.getChildren().addAll(title, meta);
            setGraphic(box);
            setText(null);
        }

        private static String safe(String s) {
            return s == null || s.isBlank() ? "TBA" : s;
        }
    }
}
