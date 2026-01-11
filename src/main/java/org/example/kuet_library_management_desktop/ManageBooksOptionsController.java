package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ManageBooksOptionsController {

    @FXML
    private void onAddBooks(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/AddBook_view.fxml", "Add Books");
    }

    @FXML
    private void onViewBooks(ActionEvent event) {
        navigateTo(event, "/org/example/kuet_library_management_desktop/BookSearch_view.fxml", "View Books");
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

