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
        openWindow(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
    }

    private void openWindow(ActionEvent event, String fxmlPath, String title) {
        try {
            // Resolve FXML URL robustly: try class resource, classloader, and filesystem fallback
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                // try without leading slash
                String noLeading = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
                fxmlUrl = getClass().getClassLoader().getResource(noLeading);
            }
            if (fxmlUrl == null) {
                // try filesystem (useful when running from IDE sources)
                File f = new File("src/main/resources" + (fxmlPath.startsWith("/") ? fxmlPath : ("/" + fxmlPath)));
                if (f.exists()) fxmlUrl = f.toURI().toURL();
            }
            if (fxmlUrl == null) {
                throw new IOException("FXML resource not found: " + fxmlPath);
            }

            // Debug: show which URL we resolved
            System.out.println("[DEBUG] Resolved FXML URL for " + fxmlPath + " -> " + fxmlUrl);

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML " + fxmlPath + ": " + e.getMessage());
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