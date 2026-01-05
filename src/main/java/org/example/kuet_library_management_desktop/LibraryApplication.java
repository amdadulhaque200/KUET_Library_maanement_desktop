package org.example.kuet_library_management_desktop;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class LibraryApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlPath = "/org/example/kuet_library_management_desktop/Library_view.fxml";

        Parent root = Navigation.load(fxmlPath);
        Scene scene = new Scene(root, 900, 650); // Set consistent size: 900x650

        // Load CSS stylesheet for styling
        loadStylesheet(scene);

        stage.setTitle("KUET Library Management");
        stage.setScene(scene);
        stage.setResizable(false); // Prevent window resizing for consistent layout
        stage.centerOnScreen();
        stage.show();
    }

    private void loadStylesheet(Scene scene) {
        try {
            var cssResource = getClass().getResource("/org/example/kuet_library_management_desktop/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("✓ CSS stylesheet loaded successfully");
            }
        } catch (Exception e) {
            // Silently continue without CSS - app will use default JavaFX styling
            System.out.println("Note: Running with default styling (CSS not loaded)");
        }
    }
}