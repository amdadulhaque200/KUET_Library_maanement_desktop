module org.example.kuet_library_management_desktop {
    requires javafx.controls;
    requires javafx.fxml;

    // If you later add other modular libraries (controlsfx, etc.) require them here.

    // Allow FXMLLoader to access controller classes in this package
    opens org.example.kuet_library_management_desktop to javafx.fxml;
    exports org.example.kuet_library_management_desktop;
}
