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

        stage.setTitle("KUET Library Management");
        stage.setScene(new Scene(root));
        stage.show();
    }
}