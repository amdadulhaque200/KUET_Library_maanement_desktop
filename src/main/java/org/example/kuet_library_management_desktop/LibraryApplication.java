package org.example.kuet_library_management_desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class LibraryApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Try several ways to locate the FXML so the app runs both from IDE and from packaged artifacts
        URL fxmlUrl = getClass().getResource("/org/example/kuet_library_management_desktop/Library_view.fxml");
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getClassLoader().getResource("org/example/kuet_library_management_desktop/Library_view.fxml");
        }
        if (fxmlUrl == null) {
            File f = new File("src/main/resources/org/example/kuet_library_management_desktop/Library_view.fxml");
            if (f.exists()) fxmlUrl = f.toURI().toURL();
        }
        if (fxmlUrl == null) {
            throw new RuntimeException("Cannot find Library_view.fxml on classpath or in src/main/resources");
        }

        // Debug: print resolved FXML URL so you can verify where it was loaded from
        System.out.println("[DEBUG] Resolved Library_view.fxml URL: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());
        stage.setTitle("KUET Library Management");
        stage.setScene(scene);
        stage.show();
    }
}