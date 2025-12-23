package org.example.kuet_library_management_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button manageBooksBtn, manageStudentsBtn, issueReturnBtn, logoutBtn;

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

    private void setContent(javafx.scene.Node node) {
        contentPane.getChildren().setAll(node);
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
        setContent(manageBooksView);
    }

    private void loadManageStudentsView() {
        org.example.kuet_library_management_desktop.StudentRepository repo = new org.example.kuet_library_management_desktop.StudentRepository();
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
            java.util.Optional<Student> opt = repo.getAll().stream().filter(s -> s.getId().equals(id)).findFirst();
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

        searchField.setOnAction(ev -> { ev.consume(); searchBtn.fire(); });

        HBox top = new HBox(8, new Label("Search by ID:"), searchField, new Region(), searchBtn);
        HBox.setHgrow(searchField, javafx.scene.layout.Priority.NEVER);
        HBox.setHgrow(new Region(), javafx.scene.layout.Priority.ALWAYS);
        top.setStyle("-fx-padding: 8;");

        VBox root = new VBox(8, top, resultPane);
        root.setStyle("-fx-padding: 10;");
        setContent(root);
    }

    private void loadIssueReturnView() {
        VBox issueReturnView = new VBox(12);
        issueReturnView.setStyle("-fx-padding: 16;");

        Label title = new Label("Issue / Return Books");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button issueBtn = new Button("Issue");
        Button returnBtn = new Button("Return");

        issueBtn.setOnAction(e -> {
            e.consume();
            TextInputDialog sDlg = new TextInputDialog();
            sDlg.setTitle("Issue Book - Student ID");
            sDlg.setHeaderText("Enter student ID");
            String sid = sDlg.showAndWait().orElse("").trim();
            if (sid.isEmpty()) { showAlert("Input error", "Student ID required", Alert.AlertType.ERROR); return; }

            TextInputDialog bDlg = new TextInputDialog();
            bDlg.setTitle("Issue Book - Book ID");
            bDlg.setHeaderText("Enter book ID (number)");
            String bidS = bDlg.showAndWait().orElse("").trim();
            if (bidS.isEmpty()) { showAlert("Input error", "Book ID required", Alert.AlertType.ERROR); return; }

            int bid;
            try { bid = Integer.parseInt(bidS); } catch (NumberFormatException ex) { showAlert("Input error", "Book ID must be a number", Alert.AlertType.ERROR); return; }

            org.example.kuet_library_management_desktop.StudentRepository repo = new org.example.kuet_library_management_desktop.StudentRepository();
            java.util.Optional<Student> sOpt = repo.getAll().stream().filter(s -> s.getId().equals(sid)).findFirst();
            if (sOpt.isEmpty()) { showAlert("Not found", "Student not found: " + sid, Alert.AlertType.ERROR); return; }

            Book book = findBookById(bid);
            if (book == null) { showAlert("Not found", "Book not found: " + bid, Alert.AlertType.ERROR); return; }
            if (!"Available".equalsIgnoreCase(book.getStatus())) { showAlert("Unavailable", "Book is not available to issue", Alert.AlertType.ERROR); return; }

            book.setStatus("Issued");
            Student st = sOpt.get();
            st.setBorrowedCount(st.getBorrowedCount() + 1);
            showAlert("Success", "Book " + bid + " issued to " + st.getName(), Alert.AlertType.INFORMATION);
        });

        returnBtn.setOnAction(e -> {
            e.consume();
            TextInputDialog bDlg = new TextInputDialog();
            bDlg.setTitle("Return Book - Book ID");
            bDlg.setHeaderText("Enter book ID (number)");
            String bidS = bDlg.showAndWait().orElse("").trim();
            if (bidS.isEmpty()) { showAlert("Input error", "Book ID required", Alert.AlertType.ERROR); return; }
            int bid;
            try { bid = Integer.parseInt(bidS); } catch (NumberFormatException ex) { showAlert("Input error", "Book ID must be a number", Alert.AlertType.ERROR); return; }

            TextInputDialog sDlg = new TextInputDialog();
            sDlg.setTitle("Return Book - Student ID");
            sDlg.setHeaderText("Enter student ID (who returns)");
            String sid = sDlg.showAndWait().orElse("").trim();
            if (sid.isEmpty()) { showAlert("Input error", "Student ID required", Alert.AlertType.ERROR); return; }

            Book book = findBookById(bid);
            if (book == null) { showAlert("Not found", "Book not found: " + bid, Alert.AlertType.ERROR); return; }
            if (!"Issued".equalsIgnoreCase(book.getStatus())) { showAlert("Invalid", "Book is not marked as issued", Alert.AlertType.ERROR); return; }

            org.example.kuet_library_management_desktop.StudentRepository repo = new org.example.kuet_library_management_desktop.StudentRepository();
            java.util.Optional<Student> sOpt = repo.getAll().stream().filter(s -> s.getId().equals(sid)).findFirst();
            if (sOpt.isEmpty()) { showAlert("Not found", "Student not found: " + sid, Alert.AlertType.ERROR); return; }

            book.setStatus("Available");
            Student st = sOpt.get();
            if (st.getBorrowedCount() > 0) st.setBorrowedCount(st.getBorrowedCount() - 1);
            showAlert("Success", "Book " + bid + " returned by " + st.getName(), Alert.AlertType.INFORMATION);
        });

        HBox buttons = new HBox(10, issueBtn, returnBtn);
        buttons.setStyle("-fx-alignment: center;");

        issueReturnView.getChildren().addAll(title, buttons);
        setContent(issueReturnView);
    }
    private Book findBookById(int id) {
        for (Book b : books) if (b.getId() == id) return b;
        return null;
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
