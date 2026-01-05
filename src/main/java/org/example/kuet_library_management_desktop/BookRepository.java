package org.example.kuet_library_management_desktop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    public BookRepository() {
    }

    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getString("status")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getString("status")
                );
                return Optional.of(book);
            }
        } catch (SQLException e) {
            System.err.println("Error finding book: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Book> search(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("isbn"),
                        rs.getString("status")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    public boolean save(Book book) {
        String insertSql = "INSERT INTO books (title, author, genre, status) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE books SET title = ?, author = ?, genre = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {

            if (book.getId() > 0) {
                // Update existing book
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, book.getTitle());
                    pstmt.setString(2, book.getAuthor());
                    pstmt.setString(3, book.getGenre());
                    pstmt.setString(4, book.getStatus());
                    pstmt.setInt(5, book.getId());
                    pstmt.executeUpdate();
                    return true;
                }
            } else {
                // Insert new book
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, book.getTitle());
                    pstmt.setString(2, book.getAuthor());
                    pstmt.setString(3, book.getGenre());
                    pstmt.setString(4, book.getStatus());
                    pstmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int bookId, String status) {
        String sql = "UPDATE books SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating book status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

