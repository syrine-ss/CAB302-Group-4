package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.Signup;
import com.cab302.vic.model.User;
import com.cab302.vic.service.*;

import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.List;

/**
 * Controller shared by "attendance register" and "hours review" screen (US-16, US-22).
 * Reads the event from {@link CoordinatorDashboardController#selectedEvent}
 */
public class EventReviewController {
    @FXML private Label eventTitle;
    @FXML private VBox signupsBox;

    private final SignupService signupService = ServiceFactory.getInstance().signups();
    private final HoursService hoursService = ServiceFactory.getInstance().hours();
    private Event reviewingEvent;
    private User coordinator;

    /**
     * Initialise the page by displaying the volunteers for the event including their hours and attendance.
     */
    @FXML
    public void initialize() {
        coordinator = SessionManager.getInstance().getCurrentUser();
        reviewingEvent = CoordinatorDashboardController.selectedEvent.get();
        eventTitle.setText(reviewingEvent.getTitle());

        refreshSignups();
    }

    private void refreshSignups() {
        List<Signup> signups = signupService.signupsForEvent(reviewingEvent.getId());

        signupsBox.getChildren().clear();

        if (signups.isEmpty()) {
            signupsBox.getChildren().add(emptyState("No volunteers are currently signed up."));
            return;
        }

        signups.forEach(s -> signupsBox.getChildren().add(signupRow(s)));
    }

    protected VBox signupRow(Signup signup) {

        VBox mainContainer = createMainContainer();
        HBox row = createRow(true);

        row.setPrefHeight(65);
        row.setMinHeight(65);
        row.setMaxHeight(65);

        mainContainer.getChildren().add(row);

        HoursEntry entry = hoursService.latestHoursForEvent(
                reviewingEvent.getId(),
                signup.getUserId()
        ).orElse(null);

        Label name = new Label(fullNameFromSignup(signup));
        name.getStyleClass().add("list-title");
        name.setPrefWidth(250);

        // all entries will list the name and hours
        showUpcomingSignup(row, name, entry);

        // if event has passed, mark attendance and review form can be shown
        if (signupService.hasPassed(reviewingEvent)) {
            showPastSignup(signup, entry, row, mainContainer);
        }

        return mainContainer;
    }

    private VBox createMainContainer() {
        VBox mainContainer = new VBox(8);
        mainContainer.setPadding(new Insets(12));
        mainContainer.setFillWidth(true);

        mainContainer.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        mainContainer.getStyleClass().add("list-card");

        return mainContainer;
    }

    private HBox createRow(boolean alignLeft) {
        HBox row = new HBox(12);

        row.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(row, Priority.ALWAYS);

        if (alignLeft) {
            row.setAlignment(Pos.CENTER_LEFT);
        }
        else {
            row.setAlignment(Pos.CENTER_RIGHT);
        }

        return row;
    }

    private VBox createRowItem(Node node) {

        VBox item = new VBox(6,node);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(item, Priority.ALWAYS);

        return item;
    }

    private Label createHoursLabel(HoursEntry entry) {
        Label hours;

        if (entry == null || entry.getHours() == 0 || entry.isRejected()) {
            hours = new Label("0 hours logged");
        } else {
            hours = new Label(
                    entry.getHours() + " hours " + (entry.isPending() ? "pending" : "logged"));
        }

        hours.getStyleClass().add("signup-hours");
        hours.setPrefWidth(120);

        return hours;
    }

    private void showUpcomingSignup(HBox row, Label name, HoursEntry hours) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox hoursInfo = createRowItem(createHoursLabel(hours));

        row.getChildren().addAll(createRowItem(name), spacer, hoursInfo);
    }

    private void showPastSignup(Signup signup, HoursEntry entry, HBox row, VBox container) {

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label attendance = attendanceLabel(signup);
        attendance.getStyleClass().add("event-meta");

        row.getChildren().addAll(
                createRowItem(spacer),
                createRowItem(attendance)
        );

        // if no approved hours yet, can mark attendance or review pending hours
        if (entry == null || entry.getHours() == 0 || !entry.isApproved()) {

            // if no hours entry, can mark attendance
            if (entry == null) {
                HBox attendanceOptions = markAttendanceButton(signup);
                row.getChildren().add(createRowItem(attendanceOptions));
            }
            // if pending hours show the review button instead - need this check in case of rejected entries
            else if (entry.isPending()) {
                showPendingHoursReview(signup, row, container);
            }
        }
    }

    private void showPendingHoursReview(Signup signup, HBox row, VBox container) {
        Button reviewButton = new Button("Review pending hours");
        reviewButton.getStyleClass().add("review-accordion-button");
        reviewButton.setPrefWidth(150);

        VBox hiddenReviewItem = createRowItem(reviewButton);
        row.getChildren().addAll(hiddenReviewItem);

        VBox hiddenContent = hiddenReview(signup);

        HBox hiddenForm = createRow(false);
        hiddenForm.getChildren().add(hiddenContent);

        // when review is clicked, a hidden form shows
        reviewButton.setOnAction(event -> {
            boolean expand = !hiddenContent.isVisible();

            hiddenContent.setVisible(expand);
            hiddenContent.setManaged(expand);

            reviewButton.setText(
                    expand ? "Hide review" : "Review pending hours"
            );
        });

        container.getChildren().addAll(hiddenForm);
    }

    private VBox hiddenReview(Signup signup) {
        ComboBox<String> reviewOption = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Approve",
                        "Deny"
                )
        );

        reviewOption.getStyleClass().add("combo-dropdown");
        reviewOption.setMaxWidth(150);

        reviewOption.setPromptText("Select an option");
        reviewOption.setMaxWidth(Double.MAX_VALUE);

        TextArea commentBox = new TextArea();
        commentBox.getStyleClass().add("review-comment");
        commentBox.setPromptText("Enter a comment...");
        commentBox.setPrefRowCount(2);
        commentBox.setPrefColumnCount(25);
        commentBox.setWrapText(true);

        Label statusMessage = new Label();
        statusMessage.getStyleClass().add("hint");

        commentBox.textProperty().addListener((observable, oldValue, newValue) -> {
            statusMessage.setText("");
        });

        Button submit = new Button("Submit");

        submit.getStyleClass().add("primary");

        HBox buttonRow = new HBox(8, statusMessage, submit);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        reviewOption.setOnAction(event -> statusMessage.setText(""));

        submit.setOnAction(event -> {
            String comment = commentBox.getText();
            String option = reviewOption.getValue();

            if (option == null) {
                showError("Please select an option first.");
            } else if (comment == null) {
                statusMessage.setText("Please enter a comment.");
            } else if ("Deny".equals(option)) {
                String msg = submitDenyOrApproval(comment, signup.getUserId(), false);
                commentBox.clear();
                statusMessage.setText(msg);
            } else if ("Approve".equals(option)) {
                String msg = submitDenyOrApproval(comment, signup.getUserId(), true);
                commentBox.clear();
                statusMessage.setText(msg);
            } else {
                showError("Unable to get review option.");
            }
        });

        // create the item for the row
        VBox hiddenContent = createRowItem(
                reviewOption
        );

        // add the comment box and submit button
        hiddenContent.getChildren().addAll(
                commentBox,
                buttonRow
        );

        hiddenContent.setPadding(new Insets(8, 0, 4, 0));
        hiddenContent.setAlignment(javafx.geometry.Pos.CENTER);
        hiddenContent.setMaxWidth(Double.MAX_VALUE);

        hiddenContent.setVisible(false);
        hiddenContent.setManaged(false);

        return hiddenContent;
    }

    public HBox markAttendanceButton(Signup signup) {
        boolean attended = signupService.signupAttended(signup, reviewingEvent);

        Button markAttended = new Button("Mark Attended");
        markAttended.getStyleClass().addAll("primary", "attendance-button");
        markAttended.setPrefWidth(150);

        Button markAbsent = new Button("Mark Absent");
        markAbsent.getStyleClass().addAll("secondary", "attendance-button");
        markAbsent.setPrefWidth(150);

        markAttended.setVisible(!attended);
        markAttended.setManaged(!attended);

        markAbsent.setVisible(attended);
        markAbsent.setManaged(attended);

        markAttended.setOnAction(e -> {
            updateAttendance(true, signup);
            refreshSignups();
        });

        markAbsent.setOnAction(e -> {
            updateAttendance(false, signup);
            refreshSignups();
        });

        HBox buttons = new HBox(12, markAttended, markAbsent);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        return buttons;
    }

    public Label attendanceLabel(Signup signup) {
        boolean attended = signupService.signupAttended(signup, reviewingEvent);

        String text = attended ? "Attended" : "Absent";

        Label attendance = new Label(text);
        attendance.getStyleClass().add("attendance-label");

        return attendance;
    }

    private String submitDenyOrApproval(String reason, int volunteerId, boolean approved) {
        try {
            int coordId = coordinator.getId();

            HoursEntry entry = hoursService
                    .latestHoursForEvent(
                            reviewingEvent.getId(),
                            volunteerId
                    ).orElse(null);

            if (entry == null) {
                return "Review unsuccessful. No pending hours found for this event.";
            }

            if (approved) {
                hoursService.approve(entry.getId(), coordId, reason);
            } else {
                hoursService.reject(entry.getId(), coordId, reason);
            }
            return "Review successful.";

        } catch (HoursException e) {
            String action = approved ? "approve" : "deny";
            showError("Could not " + action + " hours entry. " + e.getMessage());
            return ("Review unsuccessful.");
        }
    }

    private void updateAttendance(boolean attended, Signup signup) {
        try {
            signupService.setAttendance(
                    reviewingEvent.getId(),
                    signup.getUserId(),
                    attended,
                    coordinator.getId()
            );
        } catch (SignupException e) {
            showError("Could not set attendance:" + e.getMessage());
        }
    }

    @FXML
    protected void onHomeClick() {
        CoordinatorDashboardController.selectedEvent.set(null);
        try {
            SceneNavigator.switchTo(eventTitle, "coordinator-dashboard.fxml", "Coordinator Dashboard");
        } catch (IOException e) {
            showError("Could not return to the dashboard: " + e.getMessage());
        }
    }

    private String fullNameFromSignup(Signup signup) {
        try {
            User user = signupService.getVolunteerFromSignup(signup);
            return user.getFullName();
        } catch (SignupException e) {
            return "Cannot find volunteer.";
        }
    }

    private static Label emptyState(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("subtitle");
        return label;
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
