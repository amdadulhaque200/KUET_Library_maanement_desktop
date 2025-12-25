package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        // Instead of showing a password dialog, navigate to a Student options page
        openWindow(event, "/org/example/kuet_library_management_desktop/StudentOptions_view.fxml", "Student");
    }

    public void openWindow(ActionEvent event, String fxmlPath, String title) {
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

            // BOM-safe load: read bytes, strip UTF-8 BOM if present, then load from a ByteArrayInputStream
            Parent root;
            try (InputStream is = fxmlUrl.openStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
                byte[] bytes = baos.toByteArray();
                // strip UTF-8 BOM if present
                if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
                    byte[] tmp = new byte[bytes.length - 3];
                    System.arraycopy(bytes, 3, tmp, 0, tmp.length);
                    bytes = tmp;
                }
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                    FXMLLoader loader = new FXMLLoader();
                    root = loader.load(bais);
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Throwable t) {
            System.err.println("Error loading FXML " + fxmlPath + ": " + t + "\nCause: " + t.getCause());
            String msg = t.getMessage() == null ? t.toString() : t.getMessage();
            showAlert("Error", "Cannot load " + fxmlPath + ". See console for details: " + msg);
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