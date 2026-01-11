package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminOptionsController {

    @FXML
    private void onLogin(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/AdminLogin_view.fxml", "Admin Login");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/AdminRegister_view.fxml", "Admin Registration");
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Navigation.open(event, fxmlPath, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

