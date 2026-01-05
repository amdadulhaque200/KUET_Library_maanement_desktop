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
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        if (nameField != null) nameField.setPromptText("Full name");
        if (rollField != null) rollField.setPromptText("Roll number");
        if (batchField != null) batchField.setPromptText("Batch/Year");
        if (emailField != null) emailField.setPromptText("Email address");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        String name = nameField.getText();
        String roll = rollField.getText();
        String batch = batchField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (name == null || name.isBlank() || roll == null || roll.isBlank() ||
            email == null || email.isBlank() || password == null || password.isBlank()) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }

        // Validate email format
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Validation Error", "Please enter a valid email address.");
            return;
        }

        Student s = new Student();
        s.setRoll(roll);
        s.setName(name);
        s.setBatch(batch);
        s.setEmail(email);
        s.setPassword(password);
        s.setBorrowedCount(0);

        StudentRepository repo = new StudentRepository();
        boolean success = repo.save(s);

        if (success) {
            showAlert("Success", "Student registered successfully! You can now login.");

            // Clear fields
            nameField.clear();
            rollField.clear();
            batchField.clear();
            emailField.clear();
            passwordField.clear();

            // Navigate to login page
            try {
                Navigation.open(event, "/org/example/kuet_library_management_desktop/StudentLogin_view.fxml", "Student Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Failed to register. Username or roll may already exist.");
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
