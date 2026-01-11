package org.example.kuet_library_management_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;


public class Navigation {
    private static final Deque<Scene> history = new ArrayDeque<>();

    public static void open(ActionEvent event, String fxmlPath, String title) throws Exception {
        Stage stage = (Stage) (((javafx.scene.Node) event.getSource()).getScene().getWindow());
        Scene current = stage.getScene();
        if (current != null) history.push(current);

        Parent root = loadFXML(fxmlPath);
        Scene scene = new Scene(root, 900, 650); // Fixed size: 900x650 for all pages

        try {
            var cssResource = Navigation.class.getResource("/org/example/kuet_library_management_desktop/styles.css");
            if (cssResource != null) {
                String css = cssResource.toExternalForm();
                scene.getStylesheets().add(css);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS stylesheet: " + e.getMessage());
        }

        stage.setTitle(title);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) (((javafx.scene.Node) event.getSource()).getScene().getWindow());
            if (!history.isEmpty()) {
                Scene prev = history.pop();
                stage.setScene(prev);
                stage.show();
            }
        } catch (Throwable t) {
            System.err.println("Navigation.goBack error: " + t.getMessage());
        }
    }
    public static Parent load(String fxmlPath) throws IOException {
        return loadFXML(fxmlPath);
    }

    private static Parent loadFXML(String fxmlPath) throws IOException {
        URL fxmlUrl = Navigation.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            String noLeading = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
            fxmlUrl = Navigation.class.getClassLoader().getResource(noLeading);
        }
        if (fxmlUrl == null) {
            File f = new File("src/main/resources" + (fxmlPath.startsWith("/") ? fxmlPath : ("/" + fxmlPath)));
            if (f.exists()) fxmlUrl = f.toURI().toURL();
        }
        if (fxmlUrl == null) throw new IOException("FXML not found: " + fxmlPath);

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
                return loader.load(bais);
            }
        }
    }
}
