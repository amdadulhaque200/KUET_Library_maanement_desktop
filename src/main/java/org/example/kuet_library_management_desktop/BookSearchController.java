package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class BookSearchController {

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Label resultsCount;

    @FXML
    private VBox resultsBox;

    private BookRepository bookRepository = new BookRepository();
    private BorrowRequestRepository borrowRequestRepository = new BorrowRequestRepository();

    @FXML
    public void initialize() {
        loadAllBooks();

        searchBtn.setOnAction(e -> searchBooks());
        clearBtn.setOnAction(e -> {
            searchField.clear();
            loadAllBooks();
        });

        searchField.setOnAction(e -> searchBooks());
    }

    private void searchBooks() {
        String query = searchField.getText().trim().toLowerCase();
        resultsBox.getChildren().clear();

        List<Book> books;
        if (query.isEmpty()) {
            books = bookRepository.getAll();
            resultsCount.setText("Showing all books");
        } else {
            books = bookRepository.getAll().stream()
                    .filter(book ->
                        book.getTitle().toLowerCase().contains(query) ||
                        book.getAuthor().toLowerCase().contains(query) ||
                        book.getGenre().toLowerCase().contains(query)
                    )
                    .toList();
            resultsCount.setText("Found " + books.size() + " book(s)");
        }

        for (Book book : books) {
            HBox bookRow = createBookRow(book);
            resultsBox.getChildren().add(bookRow);
        }

        if (books.isEmpty()) {
            Label noResults = new Label("No books found");
            noResults.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
            resultsBox.getChildren().add(noResults);
        }
    }

    private HBox createBookRow(Book book) {
        HBox row = new HBox(15);
        row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px;");
        row.setAlignment(Pos.CENTER_LEFT);

        // Book info section
        VBox bookInfo = new VBox(5);

        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        Label authorLabel = new Label("by " + book.getAuthor() + " | " + book.getGenre());
        authorLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        Label idLabel = new Label("Book ID: " + book.getId());
        idLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

        // Status badge
        Label statusLabel = new Label(book.getStatus());
        String statusColor = book.getStatus().equals("Available") ? "#4CAF50" : "#e74c3c";
        statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: " + statusColor + "; -fx-padding: 4 8; -fx-background-radius: 3px; -fx-font-size: 12px; -fx-font-weight: bold;");

        bookInfo.getChildren().addAll(titleLabel, authorLabel, idLabel, statusLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Borrow button (only for students)
        HBox buttonBox = new HBox();
        if (SessionManager.isStudentLoggedIn()) {
            Button borrowBtn = new Button("Request Borrow");
            boolean isAvailable = book.getStatus().equals("Available");
            borrowBtn.setDisable(!isAvailable);

            String buttonColor = isAvailable ? "#3498db" : "#bdc3c7";
            borrowBtn.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: " + (isAvailable ? "hand" : "default") + "; -fx-background-radius: 4px; -fx-font-size: 14px;");

            borrowBtn.setOnAction(e -> {
                e.consume();
                handleBorrowBook(book);
            });

            buttonBox.getChildren().add(borrowBtn);
        }

        row.getChildren().addAll(bookInfo, spacer, buttonBox);

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px;"));

        return row;
    }

    // ...existing code...

    private void handleBorrowBook(Book book) {
        Student currentStudent = SessionManager.getCurrentStudent();

        if (currentStudent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student logged in!");
            return;
        }

        if (!book.getStatus().equals("Available")) {
            showAlert(Alert.AlertType.WARNING, "Not Available", "This book is not available for borrowing.");
            return;
        }

        // Create a borrow request instead of directly issuing
        BorrowRequest request = new BorrowRequest(
            book.getId(),
            currentStudent.getRoll(),
            currentStudent.getName(),
            book.getTitle()
        );

        boolean success = borrowRequestRepository.createRequest(request);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Request Submitted",
                     "Your request to borrow '" + book.getTitle() + "' has been submitted.\n\n" +
                     "Please wait for admin approval. You will be notified once approved.");

            // Refresh the search results
            searchBooks();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit borrow request. Please try again.");
        }
    }

    private void loadAllBooks() {
        searchBooks();
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

