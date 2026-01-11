package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField idNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void onRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate inputs
        if (name.isEmpty() || idNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
            return;
        }

        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address!");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters long!");
            return;
        }

        // Check if email or ID number already exists
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM admins WHERE email = ? OR id_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            checkStmt.setString(2, idNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "An admin with this email or ID number already exists!");
                return;
            }

            // Insert new admin
            String insertSql = "INSERT INTO admins (name, id_number, email, password) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, name);
            insertStmt.setString(2, idNumber);
            insertStmt.setString(3, email);
            insertStmt.setString(4, password);
            insertStmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                     "Admin account created successfully! You can now login.");

            // Navigate to login page
            try {
                Navigation.open(event, "/org/example/kuet_library_management_desktop/AdminLogin_view.fxml", "Admin Login");
            } catch (Exception navException) {
                navException.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to login page: " + navException.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register admin: " + e.getMessage());
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

