
package com.cab302.vic.controller;

import com.cab302.vic.model.Event;
import com.cab302.vic.model.HoursEntry;
import com.cab302.vic.model.User;
import com.cab302.vic.service.EventService;
import com.cab302.vic.service.HoursService;
import com.cab302.vic.service.ServiceFactory;
import com.cab302.vic.service.SignupService;
import com.cab302.vic.util.EventDisplay;
import com.cab302.vic.util.SceneNavigator;
import com.cab302.vic.util.SessionManager;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    @FXML private HBox userInfo;
    @FXML private VBox currentEvents;
    @FXML private VBox pastEvents;
    @FXML private Button homeButton;

    private final EventService eventService = ServiceFactory.getInstance().events();
    private final SignupService signupService = ServiceFactory.getInstance().signups();
    private final HoursService hoursService = ServiceFactory.getInstance().hours();

    private List<Event> current = new ArrayList<>();
    private List<Event> past = new ArrayList<>();

    protected final User user =  SessionManager.getInstance().getCurrentUser();
    protected User.Role userRole = user.getRole();

    /**
     * Initialise the profile page.
     */
    @FXML
    public void initialize() {
        if (user == null) {
            return;
        }

        List<Event> allEvents;

        if (userRole == User.Role.VOLUNTEER) {
            allEvents = eventService.findAll();

            current = EventDisplay.upcoming(allEvents, signupService::hasPassed).stream()
                    .filter(e -> signupService.isSignedUp(e.getId(), user.getId()
                    ))
                    .toList();

            past = EventDisplay.past(allEvents, signupService::hasPassed).stream()
                    .filter(e -> signupService.isSignedUp(e.getId(), user.getId()
                    ))
                    .toList();
        }

        else {
            allEvents = eventService.findByCoordinator(user.getId());

            current = EventDisplay.upcoming(allEvents, signupService::hasPassed);

            past = EventDisplay.past(allEvents, signupService::hasPassed);
        }

        displayProfile();
    }

    private void displayProfile() {
        displayUserInfo();
        displayCurrentEvents();
        displayPastEvents();
    }

    private void displayUserInfo() {
        userInfo.getChildren().clear();

        VBox nameBox = createInfoItem("Name", user.getFullName());

        VBox emailBox = createInfoItem("Email", user.getEmail());
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        HBox.setHgrow(emailBox, Priority.ALWAYS);

        userInfo.getChildren().addAll(nameBox, emailBox);

        // if volunteer - display total hours
        if(userRole == User.Role.VOLUNTEER) {
            double total = hoursService.totalApprovedHours(user.getId());

            VBox hoursBox = createInfoItem("Total approved hours", String.valueOf(total));
            HBox.setHgrow(hoursBox, Priority.ALWAYS);
            userInfo.getChildren().add(hoursBox);
        }

        // if coordinator - display amount of events hosted
        if(userRole == User.Role.COORDINATOR) {
            int total = eventService.findByCoordinator(user.getId()).size();

            VBox eventBox = createInfoItem("Total events hosted", String.valueOf(total));
            HBox.setHgrow(eventBox, Priority.ALWAYS);
            userInfo.getChildren().add(eventBox);
        }


    }

    private VBox createInfoItem(String heading, String value) {
        Label headingLabel = new Label(heading);
        headingLabel.getStyleClass().add("section-heading");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("event-meta");
        valueLabel.setWrapText(true);

        VBox item = new VBox(6, headingLabel, valueLabel);
        item.getStyleClass().add("event-meta");
        item.setAlignment(Pos.CENTER_LEFT);
        item.setMaxWidth(Double.MAX_VALUE);

        return item;
    }

    private void displayCurrentEvents() {
        currentEvents.getChildren().clear();

        currentEvents.getChildren().add(
                boxHeading("Current (" + current.size() + ")")
        );

        if (current.isEmpty()) {
            currentEvents.getChildren().add(
                    emptyList("You have no current events.")
            );
            return;
        }

        current.forEach(event ->
                currentEvents.getChildren().add(
                        card(event)
                )
        );
    }

    private void displayPastEvents() {
        pastEvents.getChildren().clear();

        pastEvents.getChildren().add(boxHeading("Previous (" + past.size() + ")"));

        if (past.isEmpty()) {
            pastEvents.getChildren().add(
                    emptyList("You have no past events yet.")
            );
            return;
        }
        
        past.forEach(event ->pastEvents.getChildren().add(card(event)));
    }

    private HBox card(Event event) {
        Label title = new Label(event.getTitle());
        title.getStyleClass().add("list-title");
        title.setWrapText(true);

        VBox details = new VBox(4, title);
        details.setAlignment(Pos.CENTER_LEFT);
        details.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(details, Priority.ALWAYS);

        HBox card = new HBox(12, details);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("list-card");
        card.setMaxWidth(Double.MAX_VALUE);

        if (userRole == User.Role.VOLUNTEER) {
            HoursEntry entry = (user == null)
                    ? null
                    : hoursService.latestHoursForEvent(event.getId(), user.getId()
            ).orElse(null);

            Label hoursAmount = displayHours(entry);
            hoursAmount.getStyleClass().add("event-meta");

            card.getChildren().add(hoursAmount);
        }

        else {
            int signupCount = signupService.spotsFilled(event);

            Label volunteerCount = new Label(signupCount + " volunteer(s)");
            volunteerCount.getStyleClass().add("event-meta");

            card.getChildren().add(volunteerCount);
        }
        return card;
    }

    private Label displayHours(HoursEntry entry) {
        Label hours;

        if (entry == null || entry.getHours() == 0 || entry.isRejected()) {
            hours = new Label("0 hours logged");
        }

        else {
            hours = new Label(
                    entry.getHours() + " hours " + (entry.isPending()
                            ? "pending" : "logged")
            );
        }

        hours.setWrapText(true);
        hours.setMinWidth(100);
        hours.setAlignment(Pos.CENTER_RIGHT);

        return hours;
    }

    protected static Label emptyList(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("subheading");
        return label;
    }

    protected static Label boxHeading(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-heading");
        return label;
    }


    @FXML
    private void onBackClicked() {
        if (userRole == User.Role.COORDINATOR) {
            returnToControllerDashboard();
        }
        else{
            returnToVolunteerDashboard();
        }
    }

    protected void returnToControllerDashboard() {
        try {
            SceneNavigator.switchTo(homeButton, "coordinator-dashboard.fxml", "Coordinator Dashboard");
        } catch (IOException e) {
            showError("Could not return to the dashboard: " + e.getMessage());
        }
    }

    protected void returnToVolunteerDashboard() {
        try {
            SceneNavigator.switchTo(homeButton, "volunteer-dashboard.fxml", "Volunteer Dashboard");
        } catch (IOException e) {
            showError("Could not return to the dashboard: " + e.getMessage());
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
                    SceneNavigator.switchTo(homeButton, "login-view.fxml", "Volunteer Impact Coordinator");
                } catch (IOException e) {
                    showError("Could not return to login: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
