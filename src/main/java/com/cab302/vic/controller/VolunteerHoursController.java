package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;
import com.cab302.vic.service.HoursException;
import com.cab302.vic.service.HoursService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A volunteer's own hours: what they have claimed, the status of each claim,
 * and the events they attended that still need hours logged.
 */
public class VolunteerHoursController {

    @FXML private Label totalLabel;
    @FXML private ListView<Row> hoursList;

    private final HoursService hoursService = ServiceFactory.getInstance().hours();

    @FXML
    public void initialize() {
        hoursList.setCellFactory(list -> new RowCell(this));
        refresh();
    }

    void refresh() {
        int me = currentUserId();
        totalLabel.setText(String.format("%.1f approved hours in total",
                hoursService.totalApprovedHoursForVolunteer(me)));
        hoursList.setItems(FXCollections.observableArrayList(buildRows(me)));
    }

    /**
     * One row per attended event: either the existing claim, or a prompt to
     * log hours where the volunteer attended but has not claimed yet.
     */
    private List<Row> buildRows(int volunteerId) {
        var signups = ServiceFactory.getInstance().signups().findByVolunteer(volunteerId);
        var events = ServiceFactory.getInstance().events();
        var claims = hoursService.findByVolunteer(volunteerId);

        List<Row> rows = new ArrayList<>();
        for (Signup signup : signups) {
            if (!signup.isAttended()) {
                continue;   // hours can only be claimed for events actually attended
            }
            Optional<Event> event = events.findById(signup.getEventId());
            if (event.isEmpty()) {
                continue;
            }
            HoursEntry claim = claims.stream()
                    .filter(c -> c.getEventId() == signup.getEventId())
                    .findFirst()
                    .orElse(null);
            rows.add(new Row(event.get(), claim));
        }
        return rows;
    }

    /** Log hours for the first time, or edit a claim still awaiting review. */
    void logOrEdit(Row row) {
        boolean editing = row.claim != null;
        TextInputDialog dialog = new TextInputDialog(
                editing ? String.valueOf(row.claim.getHours()) : "");
        dialog.setTitle(editing ? "Update hours" : "Log hours");
        dialog.setHeaderText(row.event.getTitle());
        dialog.setContentText("Hours volunteered:");

        dialog.showAndWait().ifPresent(input -> {
            double hours;
            try {
                hours = Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                error("Please enter a number, for example 3.5");
                return;
            }
            try {
                if (editing) {
                    hoursService.updateHours(row.claim.getId(), currentUserId(), hours);
                } else {
                    hoursService.logHours(currentUserId(), row.event.getId(), hours);
                }
                refresh();
            } catch (HoursException e) {
                error(e.getMessage());
            }
        });
    }

    @FXML
    protected void onBackClick() {
        try {
            SceneNavigator.switchTo(totalLabel, "volunteer-dashboard.fxml", "Volunteer Dashboard");
        } catch (IOException e) {
            error("Could not go back: " + e.getMessage());
        }
    }

    private static int currentUserId() {
        return SessionManager.getInstance().getCurrentUser().getId();
    }

    private void error(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    /** An attended event paired with its hours claim, if one has been made. */
    static class Row {
        final Event event;
        final HoursEntry claim;

        Row(Event event, HoursEntry claim) {
            this.event = event;
            this.claim = claim;
        }
    }

    private static class RowCell extends ListCell<Row> {

        private final VolunteerHoursController parent;

        RowCell(VolunteerHoursController parent) {
            this.parent = parent;
        }

        @Override
        protected void updateItem(Row row, boolean empty) {
            super.updateItem(row, empty);
            if (empty || row == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Label title = new Label(row.event.getTitle());
            title.getStyleClass().add("event-title");

            Label when = new Label(row.event.getEventDate() + "  |  " + row.event.getLocation());
            when.getStyleClass().add("event-meta");

            Label status = new Label(describe(row));
            status.getStyleClass().add(styleFor(row));
            status.setWrapText(true);

            Button action = new Button(row.claim == null ? "Log hours" : "Edit");
            action.getStyleClass().add(row.claim == null ? "primary" : "secondary");
            // Once reviewed, the figure is locked, so there is nothing to edit.
            action.setDisable(row.claim != null && !row.claim.isPending());
            action.setOnAction(e -> parent.logOrEdit(row));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox footer = new HBox(10, status, spacer, action);

            VBox box = new VBox(4, title, when, footer);
            box.getStyleClass().add("event-card");
            setGraphic(box);
            setText(null);
        }

        private static String describe(Row row) {
            if (row.claim == null) {
                return "No hours logged yet";
            }
            switch (row.claim.getStatus()) {
                case APPROVED:
                    return String.format("%.1f hours approved", row.claim.getHours());
                case REJECTED:
                    return String.format("%.1f hours rejected: %s",
                            row.claim.getHours(), row.claim.getReviewNote());
                default:
                    return String.format("%.1f hours awaiting review", row.claim.getHours());
            }
        }

        private static String styleFor(Row row) {
            if (row.claim == null) return "event-meta";
            switch (row.claim.getStatus()) {
                case APPROVED: return "success";
                case REJECTED: return "error";
                default: return "event-meta";
            }
        }
    }
}
