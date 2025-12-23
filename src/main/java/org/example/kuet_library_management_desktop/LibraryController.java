package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class LibraryController {

    @FXML
    private void openAdminView(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("Enter admin password");
        dialog.setContentText("Password:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String entered = result.get();
            String stored = PasswordStore.getPassword();
            if (stored.equals(entered)) {
                openWindow(event, "/org/example/kuet_library_management_desktop/Admin_view.fxml", "Admin Dashboard");
            } else {
                showAlert("Authentication Failed", "Incorrect password. Access denied.");
            }
        }
    }

    @FXML
    private void openStudentView(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Student Login");
        dialog.setHeaderText("Enter student password");
        dialog.setContentText("Password:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String entered = result.get();
            if ("1122".equals(entered)) {
                openWindow(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
            } else {
                showAlert("Authentication Failed", "Incorrect password. Access denied.");
            }
        }
    }

    private void openWindow(ActionEvent event, String fxmlPath, String title) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                String noLeading = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
                fxmlUrl = getClass().getClassLoader().getResource(noLeading);
            }
            if (fxmlUrl == null) {
                File f = new File("src/main/resources" + (fxmlPath.startsWith("/") ? fxmlPath : ("/" + fxmlPath)));
                if (f.exists()) fxmlUrl = f.toURI().toURL();
            }
            if (fxmlUrl == null) {
                throw new IOException("FXML resource not found: " + fxmlPath);
            }

            System.out.println("[DEBUG] Resolved FXML URL for " + fxmlPath + " -> " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Throwable t) {
            System.err.println("Error loading FXML " + fxmlPath + ": " + t + "\nCause: " + t.getCause());
            t.printStackTrace();
            showAlert("Error", "Cannot load " + fxmlPath + ". See console for details: " + t.toString());
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