package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LibraryController {

    // Shared admin password for all admins; change as needed
    private static final String ADMIN_PASSWORD = "admin123";

    @FXML
    private void openAdminView(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("Enter admin password");
        dialog.setContentText("Password:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String entered = result.get();
            if (ADMIN_PASSWORD.equals(entered)) {
                openWindow(event, "/org/example/kuet_library_management_desktop/Admin_view.fxml", "Admin Dashboard");
            } else {
                showAlert("Authentication Failed", "Incorrect password. Access denied.");
            }
        }
    }

    @FXML
    private void openStudentView(ActionEvent event) {
        openWindow(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
    }

    private void openWindow(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load " + fxmlPath + ". Check the file path and existence.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}