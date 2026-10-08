package com.cab302.vic;

import com.cab302.vic.dao.DatabaseManager;
import com.cab302.vic.util.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point. Initialises the SQLite schema on startup, then
 * shows the login view.
 */
public class VolunteerImpactApp extends Application {

    public static final String APP_TITLE = "Volunteer Impact Coordinator";

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseManager.getInstance().initialise();
        SceneNavigator.switchTo(stage, "login-view.fxml", APP_TITLE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
