package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class StudentRegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField rollField;
    @FXML
    private TextField batchField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        if (nameField != null) nameField.setPromptText("Full name");
        if (rollField != null) rollField.setPromptText("Roll number");
        if (batchField != null) batchField.setPromptText("Batch/Year");
        if (usernameField != null) usernameField.setPromptText("Choose username");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        String name = nameField.getText();
        String roll = rollField.getText();
        String batch = batchField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (name == null || name.isBlank() || roll == null || roll.isBlank()) {
            showAlert("Validation Error", "Name and roll are required.");
            return;
        }

        Student s = new Student(roll, name, username == null ? "" : username, 0);
        StudentRepository repo = new StudentRepository();
        repo.save(s);

        showAlert("Success", "Student registered: " + roll);

        try {
            Navigation.open(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
