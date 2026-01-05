package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class StudentLoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private StudentRepository studentRepository = new StudentRepository();

    @FXML
    public void initialize() {
        if (emailField != null) emailField.setPromptText("Email");
        if (passwordField != null) passwordField.setPromptText("Password");
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showAlert("Validation Error", "Email and password are required.", Alert.AlertType.ERROR);
            return;
        }

        if (studentRepository.authenticate(email, password)) {
            // Store the logged-in student
            Optional<Student> student = studentRepository.findByEmail(email);
            if (student.isPresent()) {
                SessionManager.setCurrentStudent(student.get());
                showAlert("Success", "Login successful!", Alert.AlertType.INFORMATION);
                try {
                    Navigation.open(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
                } catch (Exception e) {
                    showAlert("Error", "Failed to open student dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Login Failed", "Invalid email or password.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

