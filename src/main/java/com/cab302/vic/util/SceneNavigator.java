package com.cab302.vic.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Helper for swapping the root of the current window between scenes.
 * Centralises FXML loading so controllers don't repeat boilerplate.
 */
public final class SceneNavigator {

    private static final String VIEW_ROOT = "/com/cab302/vic/view/";
    private static final String STYLE_PATH = "/com/cab302/vic/styles/app.css";

    private SceneNavigator() {}

    /** Load a new scene from an FXML file and show it in the given window. */
    public static <T> T switchTo(Stage stage, String fxmlName, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(VIEW_ROOT + fxmlName));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 620);
        addStylesheetIfAvailable(scene);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        return loader.getController();
    }

    /** Convenience for controllers that already hold a Node. */
    public static <T> T switchTo(Node source, String fxmlName, String title) throws IOException {
        Stage stage = (Stage) source.getScene().getWindow();
        return switchTo(stage, fxmlName, title);
    }

    private static void addStylesheetIfAvailable(Scene scene) {
        var url = SceneNavigator.class.getResource(STYLE_PATH);
        if (url != null) {
            scene.getStylesheets().add(url.toExternalForm());
        }
    }
}
