package org.example.kuet_library_management_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class StudentController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button profileBtn, bookSearchBtn, issuedBooksBtn, logoutBtn;

    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private final ObservableList<Book> issuedBooks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            System.out.println("[DEBUG] StudentController.initialize - contentPane=" + (contentPane!=null)
                    + ", profileBtn=" + (profileBtn!=null)
                    + ", bookSearchBtn=" + (bookSearchBtn!=null)
                    + ", issuedBooksBtn=" + (issuedBooksBtn!=null)
                    + ", logoutBtn=" + (logoutBtn!=null));
            // protect initialization so any exception is logged clearly
            loadProfileView();

            profileBtn.setOnAction(evt -> loadProfileView());
            bookSearchBtn.setOnAction(evt -> loadBookSearchView());
            issuedBooksBtn.setOnAction(evt -> loadIssuedBooksView());
            logoutBtn.setOnAction(evt -> handleLogout());

            initBooks();
        } catch (Throwable t) {
            System.err.println("Exception during StudentController.initialize: " + t);
            t.printStackTrace();
            // rethrow so FXMLLoader still gets the error if needed
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    private void initBooks() {
        books.addAll(
                new Book(1, "Java Programming", "Author A", "Programming", "Available"),
                new Book(2, "Data Structures", "Author B", "CS", "Available"),
                new Book(3, "Algorithms", "Author C", "CS", "Available")
        );
    }

    private void loadProfileView() {
        try {
            URL url = resolveResource("/org/example/kuet_library_management_desktop/Profile_view.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
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

            VBox root = (VBox) node;
            TableView<Book> table = (TableView<Book>) root.lookup("#booksTable");
            TextField searchField = (TextField) root.lookup("#searchField");
            Button searchBtn = (Button) root.lookup("#searchBtn");

            TableColumn<Book, Integer> idCol = (TableColumn<Book, Integer>) table.getColumns().get(0);
            TableColumn<Book, String> titleCol = (TableColumn<Book, String>) table.getColumns().get(1);
            TableColumn<Book, String> authorCol = (TableColumn<Book, String>) table.getColumns().get(2);
            TableColumn<Book, String> genreCol = (TableColumn<Book, String>) table.getColumns().get(3);
            TableColumn<Book, String> statusCol = (TableColumn<Book, String>) table.getColumns().get(4);

            idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
            authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));
            genreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGenre()));
            statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

            table.setItems(books);

            searchBtn.setOnAction(evt -> {
                String query = searchField.getText().toLowerCase();
                ObservableList<Book> filtered = FXCollections.observableArrayList();
                for (Book b : books) {
                    if (b.getTitle().toLowerCase().contains(query) ||
                            b.getAuthor().toLowerCase().contains(query) ||
                            b.getGenre().toLowerCase().contains(query)) {
                        filtered.add(b);
                    }
                }
                table.setItems(filtered);
            });

            table.setRowFactory(tv -> {
                TableRow<Book> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Book selected = row.getItem();
                        if (!issuedBooks.contains(selected) && selected.getStatus().equals("Available")) {
                            selected.setStatus("Issued");
                            issuedBooks.add(selected);
                            table.refresh();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book borrowed: " + selected.getTitle());
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Book already issued!");
                            alert.showAndWait();
                        }
                    }
                });
                return row;
            });

            contentPane.getChildren().setAll(node);

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
            TableView<Book> table = (TableView<Book>) root.lookup("#issuedBooksTable");
            TableColumn<Book, Integer> idCol = (TableColumn<Book, Integer>) table.getColumns().get(0);
            TableColumn<Book, String> titleCol = (TableColumn<Book, String>) table.getColumns().get(1);
            TableColumn<Book, String> authorCol = (TableColumn<Book, String>) table.getColumns().get(2);
            TableColumn<Book, String> genreCol = (TableColumn<Book, String>) table.getColumns().get(3);
            TableColumn<Book, String> statusCol = (TableColumn<Book, String>) table.getColumns().get(4);

            idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
            authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));
            genreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGenre()));
            statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

            table.setItems(issuedBooks);

            contentPane.getChildren().setAll(node);

        } catch (IOException e) {
            System.err.println("Failed to load IssuedBooks_view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // resolves a resource by trying class resource, classloader, and filesystem paths
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
        System.out.println("Logging out...");
    }
}
