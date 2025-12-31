module org.example.kuet_library_management_desktop {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.kuet_library_management_desktop to javafx.fxml;
    exports org.example.kuet_library_management_desktop;
}
