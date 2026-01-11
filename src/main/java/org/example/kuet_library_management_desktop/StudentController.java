package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class StudentController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button profileBtn, bookSearchBtn, issuedBooksBtn, logoutBtn;

    @FXML
    private Label welcomeLabel;

    private BookRepository bookRepository = new BookRepository();
    private IssuedBookRepository issuedBookRepository = new IssuedBookRepository();

    @FXML
    public void initialize() {
        // Set welcome message with logged-in student's name
        Student currentStudent = SessionManager.getCurrentStudent();
        if (currentStudent != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentStudent.getName() + "!");
        }

        loadProfileView();
        profileBtn.setOnAction(evt -> { evt.consume(); loadProfileView(); });
        bookSearchBtn.setOnAction(evt -> { evt.consume(); loadBookSearchView(); });
        issuedBooksBtn.setOnAction(evt -> { evt.consume(); loadIssuedBooksView(); });
        logoutBtn.setOnAction(evt -> { evt.consume(); handleLogout(); });
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }


    private void loadProfileView() {
        try {
            URL url = resolveResource("/org/example/kuet_library_management_desktop/Profile_view.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Node node = loader.load();
            Student currentStudent = SessionManager.getCurrentStudent();

            if (currentStudent != null) {
                VBox profileRoot = (VBox) node;

                Label nameLabel = (Label) profileRoot.lookup("#nameLabel");
                Label rollLabel = (Label) profileRoot.lookup("#rollLabel");
                Label batchLabel = (Label) profileRoot.lookup("#batchLabel");
                Label emailLabel = (Label) profileRoot.lookup("#emailLabel");
                Label borrowedLabel = (Label) profileRoot.lookup("#borrowedLabel");

                if (nameLabel != null) nameLabel.setText("Name: " + currentStudent.getName());
                if (rollLabel != null) rollLabel.setText("Roll: " + currentStudent.getRoll());
                if (batchLabel != null) batchLabel.setText("Batch: " + (currentStudent.getBatch() != null ? currentStudent.getBatch() : "N/A"));
                if (emailLabel != null) emailLabel.setText("Email: " + currentStudent.getEmail());
                if (borrowedLabel != null) borrowedLabel.setText("Books Borrowed: " + currentStudent.getBorrowedCount());
            } else {
                VBox errorBox = new VBox(10);
                errorBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
                Label errorLabel = new Label("No student logged in. Please login first.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                errorBox.getChildren().add(errorLabel);
                node = errorBox;
            }

            setContent(node);
        } catch (IOException e) {
            System.err.println("Failed to load Profile_view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBookSearchView() {
        try {
            URL url = resolveResource("/org/example/kuet_library_management_desktop/BookSearch_view.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Node node = loader.load();

            // The BookSearch_view now has its own controller that handles everything
            // Just load and display it
            setContent(node);


        } catch (IOException e) {
            System.err.println("Failed to load BookSearch_view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadIssuedBooksView() {
        try {
            URL url = resolveResource("/org/example/kuet_library_management_desktop/IssuedBooks_view.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Node node = loader.load();

            VBox root = (VBox) node;
            Label issuedCount = (Label) root.lookup("#issuedCount");
            VBox issuedBooksBox = (VBox) root.lookup("#issuedBooksBox");

            if (issuedBooksBox == null) {
                issuedBooksBox = new VBox(8);
                root.getChildren().add(issuedBooksBox);
            }

            populateIssued(issuedBooksBox, issuedCount);
            setContent(root);

        } catch (IOException e) {
            System.err.println("Failed to load IssuedBooks_view.fxml: " + e.getMessage());
            e.printStackTrace();
            // Fallback to old method
            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 20;");
            Label title = new Label("Issued Books");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            VBox list = new VBox(8);
            root.getChildren().addAll(title, list);
            populateIssued(list, null);
            setContent(root);
        }
    }

    private void populateResults(VBox resultsBox, String query, Label resultsCount) {
        resultsBox.getChildren().clear();

        List<Book> books = (query == null || query.isEmpty())
            ? bookRepository.getAll()
            : bookRepository.search(query);

        if (resultsCount != null) {
            if (query == null || query.isEmpty()) {
                resultsCount.setText("Showing all books (" + books.size() + " total)");
            } else {
                resultsCount.setText("Found " + books.size() + " book(s) matching '" + query + "'");
            }
        }

        if (books.isEmpty()) {
            Label noResults = new Label("No books found matching your search.");
            noResults.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
            resultsBox.getChildren().add(noResults);
            return;
        }

        for (Book b : books) {
            HBox row = new HBox(15);
            row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px;");

            // Add hover effect
            row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 15; -fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px; -fx-cursor: hand;"));
            row.setOnMouseExited(e -> row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-radius: 4px;"));

            VBox bookInfo = new VBox(5);
            Label titleLabel = new Label(b.getTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

            Label authorLabel = new Label("by " + b.getAuthor() + " | " + b.getGenre());
            authorLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

            Label isbnLabel = new Label("ISBN: " + (b.getIsbn() != null ? b.getIsbn() : "N/A"));
            isbnLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

            HBox statusBox = new HBox(5);
            Label statusLabel = new Label(b.getStatus());
            String statusColor = b.getStatus().equals("Available") ? "#4CAF50" : "#e74c3c";
            statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: " + statusColor + "; -fx-padding: 4 8; -fx-background-radius: 3px; -fx-font-size: 12px; -fx-font-weight: bold;");
            statusBox.getChildren().add(statusLabel);

            bookInfo.getChildren().addAll(titleLabel, authorLabel, isbnLabel, statusBox);

            Button issueBtn = new Button("Issue Book");
            issueBtn.setDisable(!b.getStatus().equals("Available"));
            issueBtn.setStyle("-fx-background-color: " + (b.getStatus().equals("Available") ? "#3498db" : "#bdc3c7") + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: " + (b.getStatus().equals("Available") ? "hand" : "default") + "; -fx-background-radius: 4px;");

            issueBtn.setOnAction(e -> {
                e.consume();
                if (SessionManager.isStudentLoggedIn()) {
                    Student currentStudent = SessionManager.getCurrentStudent();
                    boolean success = issuedBookRepository.issueBook(b.getId(), currentStudent.getRoll());
                    if (success) {
                        showAlert("Success", "Book issued successfully!", Alert.AlertType.INFORMATION);
                        populateResults(resultsBox, query, resultsCount);
                    } else {
                        showAlert("Error", "Failed to issue book.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Error", "Please login first.", Alert.AlertType.ERROR);
                }
            });

            row.getChildren().addAll(bookInfo, issueBtn);
            HBox.setHgrow(bookInfo, javafx.scene.layout.Priority.ALWAYS);
            resultsBox.getChildren().add(row);
        }
    }

    private void populateIssued(VBox list, Label issuedCount) {
        list.getChildren().clear();

        if (!SessionManager.isStudentLoggedIn()) {
            Label noLogin = new Label("Please login to view issued books.");
            noLogin.setStyle("-fx-text-fill: #666;");
            list.getChildren().add(noLogin);
            if (issuedCount != null) {
                issuedCount.setText("Please login first");
            }
            return;
        }

        Student currentStudent = SessionManager.getCurrentStudent();
        List<IssuedBook> issuedBooks = issuedBookRepository.getIssuedBooksByStudent(currentStudent.getRoll());

        if (issuedCount != null) {
            if (issuedBooks.isEmpty()) {
                issuedCount.setText("You have no issued books");
            } else {
                issuedCount.setText("You have " + issuedBooks.size() + " book(s) issued");
            }
        }

        if (issuedBooks.isEmpty()) {
            Label noBooks = new Label("No books currently issued.");
            noBooks.setStyle("-fx-text-fill: #666;");
            list.getChildren().add(noBooks);
            return;
        }

        for (IssuedBook ib : issuedBooks) {
            HBox row = new HBox(10);
            row.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

            VBox bookInfo = new VBox(5);
            Label titleLabel = new Label(ib.getBookTitle());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label dateLabel = new Label("Issued: " + ib.getIssueDate().toLocalDate() + " | Due: " + ib.getDueDate().toLocalDate());
            dateLabel.setStyle("-fx-text-fill: #666;");
            bookInfo.getChildren().addAll(titleLabel, dateLabel);

            Button returnBtn = new Button("Return");
            returnBtn.setOnAction(e -> {
                e.consume();
                boolean success = issuedBookRepository.returnBook(ib.getId());
                if (success) {
                    showAlert("Success", "Book returned successfully!", Alert.AlertType.INFORMATION);
                    populateIssued(list, issuedCount);
                } else {
                    showAlert("Error", "Failed to return book.", Alert.AlertType.ERROR);
                }
            });

            row.getChildren().addAll(bookInfo, returnBtn);
            HBox.setHgrow(bookInfo, javafx.scene.layout.Priority.ALWAYS);
            list.getChildren().add(row);
        }
    }
    private URL resolveResource(String path) throws IOException {
        URL url = getClass().getResource(path);
        if (url == null) {
            String noLeading = path.startsWith("/") ? path.substring(1) : path;
            url = getClass().getClassLoader().getResource(noLeading);
        }
        if (url == null) {
            File f = new File("src/main/resources" + (path.startsWith("/") ? path : ("/" + path)));
            if (f.exists()) url = f.toURI().toURL();
        }
        if (url == null) throw new IOException("Resource not found: " + path);
        return url;
    }

    private void handleLogout() {
        SessionManager.clearSession();
        showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
        try {
            Parent root = Navigation.load("/org/example/kuet_library_management_desktop/Library_view.fxml");
            Stage stage = (Stage) contentPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("KUET Library Management");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
