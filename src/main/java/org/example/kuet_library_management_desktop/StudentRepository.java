package org.example.kuet_library_management_desktop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    public StudentRepository() {
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("roll"),
                        rs.getString("name"),
                        rs.getString("batch"),
                        rs.getString("email"),
                        rs.getInt("borrowed_count")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    public Optional<Student> findById(String roll) {
        String sql = "SELECT * FROM students WHERE roll = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roll);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("roll"),
                        rs.getString("name"),
                        rs.getString("batch"),
                        rs.getString("email"),
                        rs.getInt("borrowed_count")
                );
                return Optional.of(student);
            }
        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE email = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("roll"),
                        rs.getString("name"),
                        rs.getString("batch"),
                        rs.getString("email"),
                        rs.getInt("borrowed_count")
                );
                student.setPassword(rs.getString("password"));
                return Optional.of(student);
            }
        } catch (SQLException e) {
            System.err.println("Error finding student by email: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean save(Student student) {
        String checkSql = "SELECT id FROM students WHERE roll = ?";
        String insertSql = "INSERT INTO students (roll, name, batch, email, password, borrowed_count) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE students SET name = ?, batch = ?, email = ?, borrowed_count = ? WHERE roll = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {

            // Check if student exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, student.getRoll());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Update existing student
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, student.getName());
                        updateStmt.setString(2, student.getBatch());
                        updateStmt.setString(3, student.getEmail());
                        updateStmt.setInt(4, student.getBorrowedCount());
                        updateStmt.setString(5, student.getRoll());
                        updateStmt.executeUpdate();
                        return true;
                    }
                } else {
                    // Insert new student
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, student.getRoll());
                        insertStmt.setString(2, student.getName());
                        insertStmt.setString(3, student.getBatch());
                        insertStmt.setString(4, student.getEmail());
                        insertStmt.setString(5, student.getPassword());
                        insertStmt.setInt(6, student.getBorrowedCount());
                        insertStmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate(String email, String password) {
        String sql = "SELECT password FROM students WHERE email = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword != null && storedPassword.equals(password);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating student: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String roll) {
        String sql = "DELETE FROM students WHERE roll = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roll);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

