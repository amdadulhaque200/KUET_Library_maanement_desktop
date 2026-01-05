package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class AdminLoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private AdminRepository adminRepository = new AdminRepository();

    @FXML
    public void initialize() {
        if (emailField != null) emailField.setPromptText("Admin Email");
        if (passwordField != null) passwordField.setPromptText("Admin Password");
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showAlert("Validation Error", "Email and password are required.", Alert.AlertType.ERROR);
            return;
        }

        if (adminRepository.authenticate(email, password)) {
            Optional<Admin> admin = adminRepository.findByEmail(email);
            if (admin.isPresent()) {
                SessionManager.setCurrentAdmin(admin.get());
                showAlert("Success", "Admin login successful!", Alert.AlertType.INFORMATION);
                try {
                    Navigation.open(event, "/org/example/kuet_library_management_desktop/Admin_view.fxml", "Admin Dashboard");
                } catch (Exception e) {
                    showAlert("Error", "Failed to open admin dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Login Failed", "Invalid admin credentials.", Alert.AlertType.ERROR);
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

