package org.example.kuet_library_management_desktop;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PasswordStore {
    private static final String FILENAME = ".kuet_admin_pass";

    private static Path getPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, FILENAME);
    }

    public static String getPassword() {
        Path p = getPath();
        if (Files.exists(p)) {
            try {
                return Files.readString(p, StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                System.err.println("Unable to read admin password file: " + e.getMessage());
            }
        }
        return "1234";
    }

    public static void setPassword(String password) {
        Path p = getPath();
        try {
            Files.writeString(p, password, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Unable to write admin password file: " + e.getMessage());
        }
    }
}
