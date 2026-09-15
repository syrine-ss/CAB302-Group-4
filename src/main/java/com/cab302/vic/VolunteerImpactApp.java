package com.cab302.vic;

import com.cab302.vic.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VolunteerImpactApp extends Application {

    public static final String APP_TITLE = "Volunteer Impact Coordinator";

    @Override
    public void start(Stage stage) throws IOException {
        // Initialise the database on startup
        DatabaseManager.getInstance().initialise();

        FXMLLoader loader = new FXMLLoader(
                VolunteerImpactApp.class.getResource("view/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
