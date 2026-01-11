package org.example.kuet_library_management_desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class LibraryApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlPath = "/org/example/kuet_library_management_desktop/Library_view.fxml";

        Parent root = null;

        // 1) Try existing Navigation.load (keep existing behavior)
        try {
            root = Navigation.load(fxmlPath);
        } catch (Throwable navErr) {
            // 2) Fall back to FXMLLoader if Navigation is missing or fails
            System.err.println("Note: Navigation.load failed or unavailable: " + navErr.getMessage());
            try {
                URL resource = getClass().getResource(fxmlPath);
                if (resource != null) {
                    root = FXMLLoader.load(resource);
                } else {
                    System.err.println("FXML resource not found at: " + fxmlPath);
                }
            } catch (IOException fxErr) {
                System.err.println("Failed to load FXML via FXMLLoader: " + fxErr.getMessage());
            }
        }

        // 3) Final fallback: minimal placeholder UI so the app still runs
        if (root == null) {
            var placeholder = new StackPane(new Label("UI failed to load. Check Navigation class and Library_view.fxml"));
            root = placeholder;
        }

        Scene scene = new Scene(root, 900, 650);

        loadStylesheet(scene);

        stage.setTitle("KUET Library Management");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private void loadStylesheet(Scene scene) {
        try {
            var cssResource = getClass().getResource("/org/example/kuet_library_management_desktop/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("✓ CSS stylesheet loaded successfully");
            } else {
                System.out.println("Note: styles.css not found at expected path.");
            }
        } catch (Exception e) {
            System.out.println("Note: Running with default styling (CSS not loaded) — " + e.toString());
        }
    }
}