package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.Signup;
import com.cab302.vic.model.User;
import com.cab302.vic.service.*;

import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.util.List;

/**
 * Controller shared by "attendance register" and "hours review" screen (US-16, US-22).
 * Reads the event from {@link CoordinatorDashboardController#selectedEvent}
 */
public class EventReviewController {
    @FXML private Label eventTitle;
    @FXML private Button Deny;
    @FXML private Button Approve;
    @FXML private ListView<Signup> signupsList;

    private final SignupService signupService = ServiceFactory.getInstance().signups();

    /**
     * Initialise the page by displaying the volunteers for the event including their hours and attendance.
     */
    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser(); // TODO remove later if no use
        Event reviewingEvent = CoordinatorDashboardController.selectedEvent.get();
        eventTitle.setText(reviewingEvent.getTitle());

        signupsList.setCellFactory(list -> new EventReviewController.SignupCell());
        refreshSignups();
    }

    private void refreshSignups() {
        Event event = CoordinatorDashboardController.selectedEvent.get();
        List<Signup> signups = signupService.signupsForEvent(event.getId());
        signupsList.setItems(FXCollections.observableArrayList(signups));
    }


    private class SignupCell extends ListCell<Signup> {

        @Override
        protected void updateItem(Signup signup, boolean empty) {
            super.updateItem(signup, empty);

            if (empty || signup == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            HBox row = new HBox(12);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.getStyleClass().add("list-card");

            Label name = new Label(fullNameFromSignup(signup));
            name.getStyleClass().add("list-title");
            name.setPrefWidth(123);

            Label hours = new Label("0 hours logged");
            hours.getStyleClass().add("signup-hours");
            hours.setPrefWidth(120);

            Button approve = new Button("Approve");

            approve.setOnAction(event -> {
                approveButtonClick();
            });

            approve.getStyleClass().add("approve-button");

            Button deny = new Button("Deny");
            deny.setOnAction(event -> {
                denyButtonClick();
            });

            deny.getStyleClass().add("deny-button");

            HBox reviewButtons = new HBox(5, approve, deny);
            reviewButtons.setAlignment(
                    javafx.geometry.Pos.CENTER_LEFT
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            ComboBox<String> attendance = new ComboBox<>(
                    FXCollections.observableArrayList(
                            "Not recorded", "Attended", "Absent"
                    )
            );

            attendance.setValue("Not recorded");
            attendance.getStyleClass().add("attendance-combo");
            attendance.setPrefWidth(120);

            row.getChildren().addAll(
                    name,
                    hours,
                    reviewButtons,
                    spacer,
                    attendance);

            setText(null);
            setGraphic(row);
        }
    }

    private void approveButtonClick() {

    }

    private void denyButtonClick() {
        final JFrame parent = new JFrame();
        JButton button = new JButton();

        button.setText("Click me to show dialog!");
        parent.add(button);
        parent.pack();
        parent.setVisible(true);

        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String name = JOptionPane.showInputDialog(parent,
                        "What is your name?", null);
            }
        });
    }

    protected void onHomeClick() {

    }

    private String fullNameFromSignup(Signup signup) {
        try {
            User user = signupService.getVolunteerFromSignup(signup);
            return user.getFullName();
        } catch (SignupException e) {
            showError("Could not get name of signup: " + e.getMessage());
            return null;
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
