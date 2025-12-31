package org.example.kuet_library_management_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
        loadProfileView();
        profileBtn.setOnAction(evt -> { evt.consume(); loadProfileView(); });
        bookSearchBtn.setOnAction(evt -> { evt.consume(); loadBookSearchView(); });
        issuedBooksBtn.setOnAction(evt -> { evt.consume(); loadIssuedBooksView(); });
        logoutBtn.setOnAction(evt -> { evt.consume(); handleLogout(); });
        initBooks();
    }

    @FXML
    private void onBack(ActionEvent event) {
        Navigation.goBack(event);
    }

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
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

            VBox root = (VBox) node;
            TextField searchField = (TextField) root.lookup("#searchField");
            Button searchBtn = (Button) root.lookup("#searchBtn");
            Node rbLookup = root.lookup("#resultsBox");
            VBox tmpResults = (rbLookup instanceof VBox) ? (VBox) rbLookup : null;
            if (tmpResults == null) {
                tmpResults = new VBox(8);
                root.getChildren().add(tmpResults);
            }
            final VBox resultsBox = tmpResults;

            searchBtn.setOnAction(evt -> { evt.consume(); String query = searchField.getText() == null ? "" : searchField.getText().toLowerCase(); populateResults(resultsBox, query); });
            populateResults(resultsBox, "");

            setContent(root);

        } catch (IOException e) {
            System.err.println("Failed to load BookSearch_view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadIssuedBooksView() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");
        Label title = new Label("Issued Books");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox list = new VBox(8);
        root.getChildren().addAll(title, list);
        populateIssued(list);
        setContent(root);
    }

    private void populateResults(VBox resultsBox, String query) {
        resultsBox.getChildren().clear();
        for (Book b : books) {
            if (query == null || query.isEmpty() || b.getTitle().toLowerCase().contains(query) ||
                    b.getAuthor().toLowerCase().contains(query) || b.getGenre().toLowerCase().contains(query)) {
                HBox row = new HBox(10);

                resultsBox.getChildren().add(row);
            }
        }
    }

    private void populateIssued(VBox list) {
        list.getChildren().clear();
        for (Book b : issuedBooks) {
            HBox row = new HBox(10);
            Label lbl = new Label("[" + b.getId() + "] " + b.getTitle() + " — " + b.getAuthor());
            Button returnBtn = new Button("Return");
            returnBtn.setOnAction(e -> {
                e.consume();
                b.setStatus("Available");
                issuedBooks.remove(b);
                populateIssued(list);
                showAlert("Success", "Book returned: " + b.getTitle(), Alert.AlertType.INFORMATION);
            });
            row.getChildren().addAll(lbl, returnBtn);
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
        System.out.println("Logging out...");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
