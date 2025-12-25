package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings("unused")
public class StudentOptionsController {

    @FXML
    private void onLogin(ActionEvent event) {
        loadScene(event, "/org/example/kuet_library_management_desktop/Student_view.fxml", "Student Dashboard");
    }

    @FXML
    private void onRegister(ActionEvent event) {
        loadScene(event, "/org/example/kuet_library_management_desktop/StudentRegister_view.fxml", "Student Registration");
    }

    private void loadScene(ActionEvent event, String fxmlPath, String title) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                String noLeading = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
                fxmlUrl = getClass().getClassLoader().getResource(noLeading);
            }
            if (fxmlUrl == null) {
                File f = new File("src/main/resources" + (fxmlPath.startsWith("/") ? fxmlPath : ("/" + fxmlPath)));
                if (f.exists()) fxmlUrl = f.toURI().toURL();
            }
            if (fxmlUrl == null) throw new IllegalArgumentException("FXML not found: " + fxmlPath);

            Parent root;
            try (InputStream is = fxmlUrl.openStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
                byte[] bytes = baos.toByteArray();
                if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
                    byte[] tmp = new byte[bytes.length - 3];
                    System.arraycopy(bytes, 3, tmp, 0, tmp.length);
                    bytes = tmp;
                }
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                    FXMLLoader loader = new FXMLLoader();
                    root = loader.load(bais);
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
