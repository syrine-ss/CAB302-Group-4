package com.cab302.vic.controller;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.dao.SqliteEventDAO;
import com.cab302.vic.model.Event;
import com.cab302.vic.model.User;
import com.cab302.vic.service.EventService;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Dashboard shown to volunteers after login.
 * Lists all upcoming public events so volunteers can browse before signing up.
 */
public class VolunteerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<Event> eventList;

    private final EventService eventService =
            new EventService(new SqliteEventDAO(DatabaseManager.getInstance()));

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName());
        eventList.setCellFactory(list -> new EventCell());
        eventList.setItems(FXCollections.observableArrayList(eventService.findAll()));
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
            box.getStyleClass().add("event-card");
            Label title = new Label(event.getTitle());
            title.getStyleClass().add("event-title");
            Label meta = new Label(event.getEventDate() + " at " + safe(event.getLocation()));
            meta.getStyleClass().add("event-meta");
            Label desc = new Label(safe(event.getDescription()));
            desc.setWrapText(true);
            desc.getStyleClass().add("event-meta");
            box.getChildren().addAll(title, meta, desc);
            setGraphic(box);
            setText(null);
        }

        private static String safe(String s) {
            return s == null || s.isBlank() ? "" : s;
        }
    }
}
