package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StudentOptionsController {

    @FXML
    private void onLogin(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/StudentLogin_view.fxml", "Student Login");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/StudentRegister_view.fxml", "Student Registration");
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
