package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings("unused")
public class StudentRegisterController {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;

    @FXML
    public void initialize() {
        if (idField != null) idField.setPromptText("e.g., S1005");
        if (nameField != null) nameField.setPromptText("Full name");
        if (emailField != null) emailField.setPromptText("email@example.com");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        String id = idField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            showAlert("Validation Error", "ID and name are required.");
            return;
        }

        Student s = new Student(id, name, email == null ? "" : email, 0);
        StudentRepository repo = new StudentRepository();
        repo.save(s);

        showAlert("Success", "Student registered: " + id);

        // navigate to student dashboard after registration
        loadScene(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
    }

    private void loadScene(ActionEvent event, String fxmlPath, String title) {
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
            if (fxmlUrl == null) throw new IllegalArgumentException("FXML not found: " + fxmlPath);

            Parent root;
            try (InputStream is = fxmlUrl.openStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
                byte[] bytes = baos.toByteArray();
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
            System.err.println("Error loading FXML " + fxmlPath + ": " + t.getMessage());
            showAlert("Error", "Cannot load " + fxmlPath + ": " + t.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
