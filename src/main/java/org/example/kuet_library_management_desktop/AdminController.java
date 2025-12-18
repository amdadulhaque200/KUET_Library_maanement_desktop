package org.example.kuet_library_management_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class AdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button dashboardBtn, manageBooksBtn, manageStudentsBtn, issueReturnBtn, logoutBtn;

    // Mock data for books
    private ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load Dashboard view by default
        loadDashboardView();

        // Button actions
        dashboardBtn.setOnAction(e -> loadDashboardView());
        manageBooksBtn.setOnAction(e -> loadManageBooksView());
        manageStudentsBtn.setOnAction(e -> loadManageStudentsView());
        issueReturnBtn.setOnAction(e -> loadIssueReturnView());
        logoutBtn.setOnAction(e -> handleLogout());

        // Initialize mock books
        initBooks();
    }

    private void initBooks() {
        books.addAll(
                new Book(1, "Java Programming", "Author A", "Programming", "Available"),
                new Book(2, "Data Structures", "Author B", "CS", "Available"),
                new Book(3, "Algorithms", "Author C", "CS", "Available"),
                new Book(4, "Database Systems", "Author D", "Database", "Available"),
                new Book(5, "Operating Systems", "Author E", "CS", "Available")
        );
    }

    // Load Dashboard view
    private void loadDashboardView() {
        VBox dashboard = new VBox(20);
        dashboard.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label totalBooks = new Label("Total Books: " + books.size());
        totalBooks.setStyle("-fx-font-size: 16px;");

        Label availableBooks = new Label("Available Books: " +
            books.stream().filter(b -> b.getStatus().equals("Available")).count());
        availableBooks.setStyle("-fx-font-size: 16px;");

        Label issuedBooks = new Label("Issued Books: " +
            books.stream().filter(b -> b.getStatus().equals("Issued")).count());
        issuedBooks.setStyle("-fx-font-size: 16px;");

        dashboard.getChildren().addAll(title, totalBooks, availableBooks, issuedBooks);
        contentPane.getChildren().setAll(dashboard);
    }

    // Load Manage Books view
    private void loadManageBooksView() {
        VBox manageBooksView = new VBox(10);
        manageBooksView.setStyle("-fx-padding: 20;");

        Label title = new Label("Manage Books");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Add book form
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        Button addBookBtn = new Button("Add Book");
        addBookBtn.setOnAction(e -> {
            if (!titleField.getText().isEmpty() && !authorField.getText().isEmpty()) {
                int newId = books.size() + 1;
                books.add(new Book(newId, titleField.getText(), authorField.getText(),
                                   genreField.getText(), "Available"));
                titleField.clear();
                authorField.clear();
                genreField.clear();
                showAlert("Success", "Book added successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Please fill in all fields!", Alert.AlertType.ERROR);
            }
        });

        manageBooksView.getChildren().addAll(title, titleField, authorField, genreField, addBookBtn);
        contentPane.getChildren().setAll(manageBooksView);
    }

    // Load Manage Students view
    private void loadManageStudentsView() {
        VBox manageStudentsView = new VBox(20);
        manageStudentsView.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Manage Students");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Student management functionality coming soon...");
        info.setStyle("-fx-font-size: 14px;");

        manageStudentsView.getChildren().addAll(title, info);
        contentPane.getChildren().setAll(manageStudentsView);
    }

    // Load Issue/Return Books view
    private void loadIssueReturnView() {
        VBox issueReturnView = new VBox(20);
        issueReturnView.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Issue/Return Books");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Book issue/return functionality coming soon...");
        info.setStyle("-fx-font-size: 14px;");

        issueReturnView.getChildren().addAll(title, info);
        contentPane.getChildren().setAll(issueReturnView);
    }

    private void handleLogout() {
        System.out.println("Admin logging out...");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

