package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button manageBooksBtn, manageStudentsBtn, issueReturnBtn, logoutBtn;

    private BookRepository bookRepository = new BookRepository();
    private StudentRepository studentRepository = new StudentRepository();
    private IssuedBookRepository issuedBookRepository = new IssuedBookRepository();

    @FXML
    public void initialize() {
        loadManageStudentsView();

        manageBooksBtn.setOnAction(e -> { e.consume(); loadManageBooksView(); });
        manageStudentsBtn.setOnAction(e -> { e.consume(); loadManageStudentsView(); });
        issueReturnBtn.setOnAction(e -> { e.consume(); loadIssueReturnView(); });
        logoutBtn.setOnAction(e -> { e.consume(); handleLogout(); });
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void setContent(javafx.scene.Node node) {
        contentPane.getChildren().setAll(node);
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
                Book newBook = new Book(0, titleField.getText(), authorField.getText(),
                                   genreField.getText(), "", "Available");
                boolean success = bookRepository.save(newBook);
                if (success) {
                    titleField.clear();
                    authorField.clear();
                    genreField.clear();
                    showAlert("Success", "Book added successfully!", Alert.AlertType.INFORMATION);
                    loadManageBooksView(); // Refresh
                } else {
                    showAlert("Error", "Failed to add book!", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Please fill in title and author fields!", Alert.AlertType.ERROR);
            }
        });

        // Show existing books
        VBox booksList = new VBox(8);
        booksList.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        Label booksTitle = new Label("Existing Books:");
        booksTitle.setStyle("-fx-font-weight: bold;");
        booksList.getChildren().add(booksTitle);

        List<Book> books = bookRepository.getAll();
        for (Book book : books) {
            HBox bookRow = new HBox(10);
            Label bookLabel = new Label(book.getId() + ". " + book.getTitle() + " by " + book.getAuthor() + " [" + book.getStatus() + "]");
            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> {
                e.consume();
                boolean success = bookRepository.delete(book.getId());
                if (success) {
                    showAlert("Success", "Book deleted!", Alert.AlertType.INFORMATION);
                    loadManageBooksView(); // Refresh
                } else {
                    showAlert("Error", "Failed to delete book!", Alert.AlertType.ERROR);
                }
            });
            bookRow.getChildren().addAll(bookLabel, deleteBtn);
            booksList.getChildren().add(bookRow);
        }

        manageBooksView.getChildren().addAll(title, titleField, authorField, genreField, addBookBtn, booksList);
        setContent(manageBooksView);
    }

    private void loadManageStudentsView() {
        TextField searchField = new TextField();
        searchField.setPromptText("Enter student roll number");

        Button searchBtn = new Button("Search");

        VBox resultPane = new VBox(6);
        resultPane.setStyle("-fx-padding: 8;");
        resultPane.getChildren().add(new Label("Enter student roll number and click Search."));

        searchBtn.setOnAction(e -> {
            e.consume();
            String roll = searchField.getText() == null ? "" : searchField.getText().trim();
            resultPane.getChildren().clear();
            if (roll.isEmpty()) {
                resultPane.getChildren().add(new Label("Please enter a student roll number to search."));
                return;
            }
            java.util.Optional<Student> opt = studentRepository.findById(roll);
            if (opt.isPresent()) {
                Student sel = opt.get();
                Label rollL = new Label("Roll: " + sel.getRoll());
                Label nameL = new Label("Name: " + sel.getName());
                Label batchL = new Label("Batch: " + sel.getBatch());
                Label emailL = new Label("Email: " + sel.getEmail());
                Label borrowedL = new Label("Books currently borrowed: " + sel.getBorrowedCount());
                resultPane.getChildren().addAll(rollL, nameL, batchL, emailL, borrowedL);
            } else {
                resultPane.getChildren().add(new Label("Student not found."));
            }
        });

        searchField.setOnAction(ev -> { ev.consume(); searchBtn.fire(); });

        // Show all students
        VBox allStudents = new VBox(8);
        allStudents.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1; -fx-max-height: 300;");
        Label allTitle = new Label("All Students:");
        allTitle.setStyle("-fx-font-weight: bold;");
        allStudents.getChildren().add(allTitle);

        List<Student> students = studentRepository.getAll();
        for (Student student : students) {
            Label studentLabel = new Label(student.getRoll() + " - " + student.getName() + " (" + student.getEmail() + ")");
            allStudents.getChildren().add(studentLabel);
        }

        HBox top = new HBox(8, new Label("Search by Roll:"), searchField, new Region(), searchBtn);
        HBox.setHgrow(searchField, javafx.scene.layout.Priority.NEVER);
        HBox.setHgrow(new Region(), javafx.scene.layout.Priority.ALWAYS);
        top.setStyle("-fx-padding: 8;");

        VBox root = new VBox(8, top, resultPane, allStudents);
        root.setStyle("-fx-padding: 10;");
        setContent(root);
    }

    private void loadIssueReturnView() {
        VBox issueReturnView = new VBox(12);
        issueReturnView.setStyle("-fx-padding: 16;");

        Label title = new Label("Issue / Return Books");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button issueBtn = new Button("Issue Book");
        Button returnBtn = new Button("Return Book");

        issueBtn.setOnAction(e -> {
            e.consume();
            TextInputDialog sDlg = new TextInputDialog();
            sDlg.setTitle("Issue Book - Student Roll");
            sDlg.setHeaderText("Enter student roll number");
            String roll = sDlg.showAndWait().orElse("").trim();
            if (roll.isEmpty()) { showAlert("Input error", "Student roll required", Alert.AlertType.ERROR); return; }

            TextInputDialog bDlg = new TextInputDialog();
            bDlg.setTitle("Issue Book - Book ID");
            bDlg.setHeaderText("Enter book ID (number)");
            String bidS = bDlg.showAndWait().orElse("").trim();
            if (bidS.isEmpty()) { showAlert("Input error", "Book ID required", Alert.AlertType.ERROR); return; }

            int bid;
            try { bid = Integer.parseInt(bidS); } catch (NumberFormatException ex) { showAlert("Input error", "Book ID must be a number", Alert.AlertType.ERROR); return; }

            java.util.Optional<Student> sOpt = studentRepository.findById(roll);
            if (sOpt.isEmpty()) { showAlert("Not found", "Student not found: " + roll, Alert.AlertType.ERROR); return; }

            java.util.Optional<Book> bookOpt = bookRepository.findById(bid);
            if (bookOpt.isEmpty()) { showAlert("Not found", "Book not found: " + bid, Alert.AlertType.ERROR); return; }

            Book book = bookOpt.get();
            if (!"Available".equalsIgnoreCase(book.getStatus())) { showAlert("Unavailable", "Book is not available to issue", Alert.AlertType.ERROR); return; }

            boolean success = issuedBookRepository.issueBook(bid, roll);
            if (success) {
                Student st = sOpt.get();
                showAlert("Success", "Book '" + book.getTitle() + "' issued to " + st.getName(), Alert.AlertType.INFORMATION);
                loadIssueReturnView(); // Refresh
            } else {
                showAlert("Error", "Failed to issue book", Alert.AlertType.ERROR);
            }
        });

        returnBtn.setOnAction(e -> {
            e.consume();

            // Show currently issued books
            List<IssuedBook> issuedBooks = issuedBookRepository.getAllIssuedBooks();
            if (issuedBooks.isEmpty()) {
                showAlert("No Books", "No books are currently issued", Alert.AlertType.INFORMATION);
                return;
            }

            StringBuilder booksList = new StringBuilder("Currently issued books:\n");
            for (IssuedBook ib : issuedBooks) {
                booksList.append(ib.getId()).append(". ").append(ib.getBookTitle())
                        .append(" - ").append(ib.getStudentName()).append("\n");
            }

            TextInputDialog ibDlg = new TextInputDialog();
            ibDlg.setTitle("Return Book - Issued Book ID");
            ibDlg.setHeaderText(booksList.toString());
            ibDlg.setContentText("Enter issued book ID:");
            String ibIdS = ibDlg.showAndWait().orElse("").trim();
            if (ibIdS.isEmpty()) { showAlert("Input error", "Issued book ID required", Alert.AlertType.ERROR); return; }

            int ibId;
            try { ibId = Integer.parseInt(ibIdS); } catch (NumberFormatException ex) { showAlert("Input error", "ID must be a number", Alert.AlertType.ERROR); return; }

            boolean success = issuedBookRepository.returnBook(ibId);
            if (success) {
                showAlert("Success", "Book returned successfully", Alert.AlertType.INFORMATION);
                loadIssueReturnView(); // Refresh
            } else {
                showAlert("Error", "Failed to return book", Alert.AlertType.ERROR);
            }
        });

        // Show currently issued books
        VBox issuedList = new VBox(8);
        issuedList.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-width: 1;");
        Label issuedTitle = new Label("Currently Issued Books:");
        issuedTitle.setStyle("-fx-font-weight: bold;");
        issuedList.getChildren().add(issuedTitle);

        List<IssuedBook> issuedBooks = issuedBookRepository.getAllIssuedBooks();
        if (issuedBooks.isEmpty()) {
            issuedList.getChildren().add(new Label("No books currently issued."));
        } else {
            for (IssuedBook ib : issuedBooks) {
                Label ibLabel = new Label(ib.getId() + ". " + ib.getBookTitle() + " - " + ib.getStudentName() +
                                        " (Due: " + ib.getDueDate().toLocalDate() + ")");
                issuedList.getChildren().add(ibLabel);
            }
        }

        HBox buttons = new HBox(10, issueBtn, returnBtn);
        buttons.setStyle("-fx-alignment: center;");

        issueReturnView.getChildren().addAll(title, buttons, issuedList);
        setContent(issueReturnView);
    }

    private void handleLogout() {
        SessionManager.clearSession();
        showAlert("Logout", "Admin logged out successfully.", Alert.AlertType.INFORMATION);
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

