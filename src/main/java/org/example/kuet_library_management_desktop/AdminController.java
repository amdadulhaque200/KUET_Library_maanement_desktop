package org.example.kuet_library_management_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class AdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button manageBooksBtn, manageStudentsBtn, issueReturnBtn, logoutBtn;
    @FXML
    private Button changePasswordBtn;

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadManageStudentsView();

        manageBooksBtn.setOnAction(e -> { e.consume(); loadManageBooksView(); });
        manageStudentsBtn.setOnAction(e -> { e.consume(); loadManageStudentsView(); });
        issueReturnBtn.setOnAction(e -> { e.consume(); loadIssueReturnView(); });
        logoutBtn.setOnAction(e -> { e.consume(); handleLogout(); });

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

    @FXML
    private void handleChangePassword(ActionEvent event) {
        event.consume();

        TextInputDialog dlg1 = new TextInputDialog();
        dlg1.setTitle("Change Admin Password");
        dlg1.setHeaderText("Enter new password");
        dlg1.setContentText("New password:");
        String newPass = dlg1.showAndWait().orElse(null);
        if (newPass == null || newPass.isEmpty()) {
            showAlert("Input error", "Password cannot be empty", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dlg2 = new TextInputDialog();
        dlg2.setTitle("Confirm Password");
        dlg2.setHeaderText("Confirm new password");
        dlg2.setContentText("Confirm password:");
        String confirm = dlg2.showAndWait().orElse(null);
        if (!newPass.equals(confirm)) {
            showAlert("Mismatch", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }

        PasswordStore.setPassword(newPass);
        showAlert("Success", "Admin password updated", Alert.AlertType.INFORMATION);
    }

    private void loadManageBooksView() {
        VBox manageBooksView = new VBox(10);
        manageBooksView.setStyle("-fx-padding: 20;");

        Label title = new Label("Manage Books");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        Button addBookBtn = new Button("Add Book");
        addBookBtn.setOnAction(e -> {
            e.consume();
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

    private void loadManageStudentsView() {
        StudentRepository repo = new StudentRepository();
        TextField searchField = new TextField();
        searchField.setPromptText("Enter student id");

        Button searchBtn = new Button("Search");

        VBox resultPane = new VBox(6);
        resultPane.setStyle("-fx-padding: 8;");
        resultPane.getChildren().add(new Label("Enter student ID and click Search."));

        searchBtn.setOnAction(e -> {
            e.consume();
            String id = searchField.getText() == null ? "" : searchField.getText().trim();
            resultPane.getChildren().clear();
            if (id.isEmpty()) {
                resultPane.getChildren().add(new Label("Please enter a student ID to search."));
                return;
            }
            java.util.Optional<Student> opt = repo.findById(id);
            if (opt.isPresent()) {
                Student sel = opt.get();
                Label idL = new Label("ID: " + sel.getId());
                Label nameL = new Label("Name: " + sel.getName());
                Label emailL = new Label("Email: " + sel.getEmail());
                Label borrowedL = new Label("Books currently borrowed: " + sel.getBorrowedCount());
                resultPane.getChildren().addAll(idL, nameL, emailL, borrowedL);
            } else {
                resultPane.getChildren().add(new Label("Student not found."));
            }
        });

        searchField.setOnAction(e -> searchBtn.fire());

        HBox top = new HBox(8, new Label("Search by ID:"), searchField, new Region(), searchBtn);
        HBox.setHgrow(searchField, javafx.scene.layout.Priority.NEVER);
        HBox.setHgrow(new Region(), javafx.scene.layout.Priority.ALWAYS);
        top.setStyle("-fx-padding: 8;");

        VBox root = new VBox(8, top, resultPane);
        root.setStyle("-fx-padding: 10;");
        contentPane.getChildren().setAll(root);
    }

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
