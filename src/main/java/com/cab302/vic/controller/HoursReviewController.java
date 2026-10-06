package com.cab302.vic.controller;

import com.cab302.vic.model.HoursEntry;
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

/**
 * Coordinator queue of hours claims awaiting a decision.
 *
 * <p>Only approved hours reach the impact totals, so this screen is what
 * stands between a volunteer's claim and the figures an organisation would
 * put in front of a funder.
 */
public class HoursReviewController {

    @FXML private Label totalLabel;
    @FXML private Label emptyLabel;
    @FXML private ListView<HoursEntry> pendingList;

    private final HoursService hoursService = ServiceFactory.getInstance().hours();

    /**
     * Initalises the display of the hours from volunteers needed for approval
     * or rejection by the coordinator of the given event.
     */
    @FXML
    public void initialize() {
        pendingList.setCellFactory(list -> new PendingCell(this));
        refresh();
    }

    void refresh() {
        int me = currentUserId();
        var pending = hoursService.findPendingForCoordinator(me);
        pendingList.setItems(FXCollections.observableArrayList(pending));

        totalLabel.setText(String.format("%.1f approved hours across your events",
                hoursService.totalApprovedHoursForCoordinator(me)));

        boolean nothingToDo = pending.isEmpty();
        emptyLabel.setText(nothingToDo ? "Nothing waiting for review." : "");
        emptyLabel.setVisible(nothingToDo);
    }

    void approve(HoursEntry entry) {
        try {
            hoursService.approve(entry.getId(), currentUserId());
            refresh();
        } catch (HoursException e) {
            error(e.getMessage());
        }
    }

    void reject(HoursEntry entry) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject hours");
        dialog.setHeaderText("Why are these hours being rejected?");
        dialog.setContentText("Reason:");

        dialog.showAndWait().ifPresent(reason -> {
            try {
                hoursService.reject(entry.getId(), currentUserId(), reason);
                refresh();
            } catch (HoursException e) {
                error(e.getMessage());
            }
        });
    }

    @FXML
    protected void onBackClick() {
        try {
            SceneNavigator.switchTo(totalLabel, "coordinator-dashboard.fxml",
                    "Coordinator Dashboard");
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

    /** One pending claim with approve and reject actions. */
    private static class PendingCell extends ListCell<HoursEntry> {

        private final HoursReviewController parent;

        PendingCell(HoursReviewController parent) {
            this.parent = parent;
        }

        @Override
        protected void updateItem(HoursEntry entry, boolean empty) {
            super.updateItem(entry, empty);
            if (empty || entry == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            String volunteer = ServiceFactory.getInstance().auth().displayName(entry.getUserId());
            String eventTitle = ServiceFactory.getInstance().events()
                    .findById(entry.getEventId())
                    .map(e -> e.getTitle())
                    .orElse("Unknown event");

            Label who = new Label(String.format("%s claimed %.1f hours", volunteer, entry.getHours()));
            who.getStyleClass().add("event-title");

            Label what = new Label(eventTitle + "  |  submitted " + entry.getLoggedOn());
            what.getStyleClass().add("event-meta");

            Button approve = new Button("Approve");
            approve.getStyleClass().add("primary");
            approve.setOnAction(e -> parent.approve(entry));

            Button reject = new Button("Reject");
            reject.getStyleClass().add("secondary");
            reject.setOnAction(e -> parent.reject(entry));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, new VBox(2, who, what), spacer, reject, approve);
            row.getStyleClass().add("event-card");

            setGraphic(row);
            setText(null);
        }
    }
}
