package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.service.SignupException;
import com.cab302.vic.service.SignupService;
import com.cab302.vic.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Coordinator view of everyone signed up to one event, and where attendance
 * is recorded once the event has happened.
 *
 * <p>Attendance matters beyond record keeping: a volunteer cannot log hours
 * until the coordinator has confirmed they turned up.
 */
public class EventSignupsController {

    @FXML private Label headingLabel;
    @FXML private Label countLabel;
    @FXML private Label hintLabel;
    @FXML private ListView<Signup> signupList;

    private final SignupService signupService = ServiceFactory.getInstance().signups();

    private Event event;

    @FXML
    public void initialize() {
        event = CoordinatorDashboardController.selectedEvent.get();
        if (event == null) {
            headingLabel.setText("No event selected");
            return;
        }
        headingLabel.setText(event.getTitle());
        signupList.setCellFactory(list -> new SignupCell(this));

        hintLabel.setText(eventHasPassed()
                ? "Tick each volunteer who attended. They can log hours once ticked."
                : "Attendance can be recorded once the event date has passed.");

        refresh();
    }

    void refresh() {
        var signups = signupService.findByEvent(event.getId());
        signupList.setItems(FXCollections.observableArrayList(signups));
        countLabel.setText(signups.size() + " of " + event.getVolunteersNeeded() + " signed up");
    }

    /** Persist an attendance tick, reverting the checkbox if the rules refuse it. */
    void setAttendance(Signup signup, boolean attended, CheckBox source) {
        try {
            signupService.markAttendance(event.getId(), signup.getUserId(), attended);
            refresh();
        } catch (SignupException e) {
            source.setSelected(!attended);   // keep the UI honest about what was saved
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    boolean eventHasPassed() {
        LocalDate date = event.parsedDate();
        return date != null && date.isBefore(LocalDate.now());
    }

    @FXML
    protected void onBackClick() {
        try {
            SceneNavigator.switchTo(headingLabel, "coordinator-dashboard.fxml",
                    "Coordinator Dashboard");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not go back: " + e.getMessage()).showAndWait();
        }
    }

    /** One volunteer, with their attendance tick. */
    private static class SignupCell extends ListCell<Signup> {

        private final EventSignupsController parent;

        SignupCell(EventSignupsController parent) {
            this.parent = parent;
        }

        @Override
        protected void updateItem(Signup signup, boolean empty) {
            super.updateItem(signup, empty);
            if (empty || signup == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            var auth = ServiceFactory.getInstance().auth();
            Label name = new Label(auth.displayName(signup.getUserId()));
            name.getStyleClass().add("event-title");

            String email = auth.findById(signup.getUserId())
                    .map(u -> u.getEmail() == null ? "" : u.getEmail())
                    .orElse("");
            Label detail = new Label((email.isBlank() ? "" : email + "  |  ")
                    + "signed up " + safe(signup.getSignedUpOn()));
            detail.getStyleClass().add("event-meta");

            CheckBox attended = new CheckBox("Attended");
            attended.setSelected(signup.isAttended());
            // Ticking before the event would let someone claim hours early.
            attended.setDisable(!parent.eventHasPassed());
            attended.setOnAction(e -> parent.setAttendance(signup, attended.isSelected(), attended));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, new VBox(2, name, detail), spacer, attended);
            row.getStyleClass().add("event-card");

            setGraphic(row);
            setText(null);
        }

        private static String safe(String s) {
            return s == null || s.isBlank() ? "recently" : s;
        }
    }
}
