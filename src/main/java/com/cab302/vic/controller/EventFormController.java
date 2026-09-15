package com.cab302.vic.controller;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.dao.SqliteEventDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.service.EventException;
import com.cab302.vic.service.EventService;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller shared by "Create Event" and "Edit Event" screens (US-02).
 * Reads the optional event from {@link CoordinatorDashboardController#selectedEvent}:
 * when it's null we're creating, when it's set we're editing.
 */
public class EventFormController {

    @FXML private Label headingLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField locationField;
    @FXML private Spinner<Integer> volunteersSpinner;
    @FXML private Label messageLabel;

    private final EventService eventService =
            new EventService(new SqliteEventDAO(DatabaseManager.getInstance()));

    private Event editingEvent;

    @FXML
    public void initialize() {
        volunteersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 5));
        // Prevent past dates in the date picker
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f2f2f2;");
                }
            }
        });

        editingEvent = CoordinatorDashboardController.selectedEvent.get();
        if (editingEvent == null) {
            headingLabel.setText("Create New Event");
        } else {
            headingLabel.setText("Edit Event");
            populateForm(editingEvent);
        }
    }

    private void populateForm(Event event) {
        titleField.setText(event.getTitle());
        descriptionArea.setText(event.getDescription());
        LocalDate parsed = event.parsedDate();
        if (parsed != null) datePicker.setValue(parsed);
        timeField.setText(event.getEventTime());
        locationField.setText(event.getLocation());
        volunteersSpinner.getValueFactory().setValue(event.getVolunteersNeeded());
    }

    @FXML
    protected void onSaveClick() {
        String date = datePicker.getValue() == null ? "" : datePicker.getValue().toString();
        int volunteers = volunteersSpinner.getValue();

        try {
            if (editingEvent == null) {
                int coordinatorId = SessionManager.getInstance().getCurrentUser().getId();
                eventService.create(titleField.getText(), descriptionArea.getText(),
                        date, timeField.getText(), locationField.getText(),
                        volunteers, coordinatorId);
            } else {
                eventService.update(editingEvent.getId(), titleField.getText(),
                        descriptionArea.getText(), date, timeField.getText(),
                        locationField.getText(), volunteers);
            }
            returnToDashboard();
        } catch (EventException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    protected void onCancelClick() {
        returnToDashboard();
    }

    private void returnToDashboard() {
        CoordinatorDashboardController.selectedEvent.set(null);
        try {
            SceneNavigator.switchTo(titleField, "coordinator-dashboard.fxml", "Coordinator Dashboard");
        } catch (IOException e) {
            showError("Could not return to the dashboard: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        messageLabel.getStyleClass().removeAll("success");
        if (!messageLabel.getStyleClass().contains("error")) {
            messageLabel.getStyleClass().add("error");
        }
        messageLabel.setText(msg);
    }
}
