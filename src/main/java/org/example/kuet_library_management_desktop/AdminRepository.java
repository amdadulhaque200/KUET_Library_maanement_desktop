package org.example.kuet_library_management_desktop;

import java.sql.*;
import java.util.Optional;

public class AdminRepository {

    public AdminRepository() {
    }

    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("name")
                );
                admin.setPassword(rs.getString("password"));
                return Optional.of(admin);
            }
        } catch (SQLException e) {
            System.err.println("Error finding admin: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean authenticate(String email, String password) {
        String sql = "SELECT password FROM admins WHERE email = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword != null && storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating admin: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean save(Admin admin) {
        String insertSql = "INSERT INTO admins (email, password, name) VALUES (?, ?, ?)";
        String updateSql = "UPDATE admins SET password = ?, name = ? WHERE email = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {

            Optional<Admin> existing = findByEmail(admin.getEmail());

            if (existing.isPresent()) {
                // Update existing admin
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, admin.getPassword());
                    pstmt.setString(2, admin.getName());
                    pstmt.setString(3, admin.getEmail());
                    pstmt.executeUpdate();
                    return true;
                }
            } else {
                // Insert new admin
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, admin.getEmail());
                    pstmt.setString(2, admin.getPassword());
                    pstmt.setString(3, admin.getName());
                    pstmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

