package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.service.*;

import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**
 * Controller for "Volunteer logs hours" screen (US-05).
 * Reads the event from {@link VolunteerDashboardController#selectedEvent}.
 */
public class LogHoursController {
    @FXML private Label headingLabel;
    @FXML private Spinner<Integer> hoursSpinner;
    @FXML private Label messageLabel;

    private final HoursService hoursService = ServiceFactory.getInstance().hours();
    private Event loggingEvent;

    /**
     * Initialises the log hours form for volunteer.
     */
    @FXML
    public void initialize() {
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 5));
        hoursSpinner.setEditable(true);
        loggingEvent = VolunteerDashboardController.selectedEvent.get();

        if (loggingEvent == null) {
            headingLabel.setText("Failed to find event.");
            return;
        }

        headingLabel.setText("Log hours for " + loggingEvent.getTitle());

        populateForm();
    }


    private void populateForm() {
        hoursSpinner.getValueFactory().setValue(0);
    }


    @FXML
    protected void onSaveClick() {
        int hours = hoursSpinner.getValue();
        int userId = SessionManager.getInstance().getCurrentUser().getId();

        try {
            hoursService.logHours(loggingEvent.getId(), userId, hours);
            returnToDashboard();

        } catch (HoursException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    protected void onCancelClick() {
        returnToDashboard();
    }

    private void returnToDashboard() {
        VolunteerDashboardController.selectedEvent.set(null);

        try {
            SceneNavigator.switchTo(headingLabel, "volunteer-dashboard.fxml", "Volunteer Dashboard");
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
