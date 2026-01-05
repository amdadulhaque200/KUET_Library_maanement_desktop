package org.example.kuet_library_management_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssuedBookRepository {

    public IssuedBookRepository() {
    }

    public List<IssuedBook> getIssuedBooksByStudent(String studentRoll) {
        List<IssuedBook> issuedBooks = new ArrayList<>();
        String sql = """
            SELECT ib.*, b.title as book_title, s.name as student_name
            FROM issued_books ib
            JOIN books b ON ib.book_id = b.id
            JOIN students s ON ib.student_roll = s.roll
            WHERE ib.student_roll = ? AND ib.status = 'Issued'
        """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentRoll);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                IssuedBook issuedBook = mapResultSetToIssuedBook(rs);
                issuedBooks.add(issuedBook);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching issued books: " + e.getMessage());
            e.printStackTrace();
        }

        return issuedBooks;
    }

    public List<IssuedBook> getAllIssuedBooks() {
        List<IssuedBook> issuedBooks = new ArrayList<>();
        String sql = """
            SELECT ib.*, b.title as book_title, s.name as student_name
            FROM issued_books ib
            JOIN books b ON ib.book_id = b.id
            JOIN students s ON ib.student_roll = s.roll
            WHERE ib.status = 'Issued'
        """;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                IssuedBook issuedBook = mapResultSetToIssuedBook(rs);
                issuedBooks.add(issuedBook);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all issued books: " + e.getMessage());
            e.printStackTrace();
        }

        return issuedBooks;
    }

    public boolean issueBook(int bookId, String studentRoll) {
        String insertSql = "INSERT INTO issued_books (book_id, student_roll, due_date) VALUES (?, ?, ?)";

        // Set due date to 14 days from now
        LocalDateTime dueDate = LocalDateTime.now().plusDays(14);

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Insert issued book record
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, bookId);
                    pstmt.setString(2, studentRoll);
                    pstmt.setTimestamp(3, Timestamp.valueOf(dueDate));
                    pstmt.executeUpdate();
                }

                // Update book status
                BookRepository bookRepo = new BookRepository();
                bookRepo.updateStatus(bookId, "Issued");

                // Update student borrowed count
                String updateStudent = "UPDATE students SET borrowed_count = borrowed_count + 1 WHERE roll = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateStudent)) {
                    pstmt.setString(1, studentRoll);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(int issuedBookId) {
        String updateSql = "UPDATE issued_books SET status = 'Returned', return_date = CURRENT_TIMESTAMP WHERE id = ?";
        String getBookIdSql = "SELECT book_id, student_roll FROM issued_books WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                int bookId = 0;
                String studentRoll = "";

                // Get book ID and student roll
                try (PreparedStatement pstmt = conn.prepareStatement(getBookIdSql)) {
                    pstmt.setInt(1, issuedBookId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                        studentRoll = rs.getString("student_roll");
                    }
                }

                // Update issued book record
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, issuedBookId);
                    pstmt.executeUpdate();
                }

                // Update book status
                BookRepository bookRepo = new BookRepository();
                bookRepo.updateStatus(bookId, "Available");

                // Update student borrowed count
                String updateStudent = "UPDATE students SET borrowed_count = borrowed_count - 1 WHERE roll = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateStudent)) {
                    pstmt.setString(1, studentRoll);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private IssuedBook mapResultSetToIssuedBook(ResultSet rs) throws SQLException {
        IssuedBook issuedBook = new IssuedBook();
        issuedBook.setId(rs.getInt("id"));
        issuedBook.setBookId(rs.getInt("book_id"));
        issuedBook.setStudentRoll(rs.getString("student_roll"));

        Timestamp issueTimestamp = rs.getTimestamp("issue_date");
        if (issueTimestamp != null) {
            issuedBook.setIssueDate(issueTimestamp.toLocalDateTime());
        }

        Timestamp dueTimestamp = rs.getTimestamp("due_date");
        if (dueTimestamp != null) {
            issuedBook.setDueDate(dueTimestamp.toLocalDateTime());
        }

        Timestamp returnTimestamp = rs.getTimestamp("return_date");
        if (returnTimestamp != null) {
            issuedBook.setReturnDate(returnTimestamp.toLocalDateTime());
        }

        issuedBook.setStatus(rs.getString("status"));
        issuedBook.setBookTitle(rs.getString("book_title"));
        issuedBook.setStudentName(rs.getString("student_name"));

        return issuedBook;
    }
}

