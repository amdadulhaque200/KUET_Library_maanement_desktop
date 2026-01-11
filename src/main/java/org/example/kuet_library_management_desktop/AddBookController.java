package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AddBookController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField copiesField;

    private BookRepository bookRepository = new BookRepository();

    @FXML
    private void onAddBook(ActionEvent event) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String copiesStr = copiesField.getText().trim();

        // Validate inputs
        if (title.isEmpty() || author.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Title and Author are required!");
            return;
        }

        if (genre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Genre/Category is required!");
            return;
        }

        // Validate and parse copies
        int totalCopies = 1; // Default to 1 copy
        if (!copiesStr.isEmpty()) {
            try {
                totalCopies = Integer.parseInt(copiesStr);
                if (totalCopies <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Total copies must be a positive number!");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Total copies must be a valid number!");
                return;
            }
        }

        // Add multiple copies of the book
        int successCount = 0;
        for (int i = 0; i < totalCopies; i++) {
            Book newBook = new Book(0, title, author, genre, "", "Available");
            boolean success = bookRepository.save(newBook);
            if (success) {
                successCount++;
            }
        }

        if (successCount == totalCopies) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                     successCount + " cop" + (successCount > 1 ? "ies" : "y") + " of the book added successfully!");
            // Clear fields
            titleField.clear();
            authorField.clear();
            genreField.clear();
            copiesField.clear();
        } else if (successCount > 0) {
            showAlert(Alert.AlertType.WARNING, "Partial Success",
                     "Only " + successCount + " out of " + totalCopies + " copies were added!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book to the database!");
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

