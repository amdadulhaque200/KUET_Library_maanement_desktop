package org.example.kuet_library_management_desktop;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowRequestRepository {

    public boolean createRequest(BorrowRequest request) {
        String sql = "INSERT INTO borrow_requests (book_id, student_roll, student_name, book_title, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, request.getBookId());
            pstmt.setString(2, request.getStudentRoll());
            pstmt.setString(3, request.getStudentName());
            pstmt.setString(4, request.getBookTitle());
            pstmt.setString(5, request.getStatus());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating borrow request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<BorrowRequest> getPendingRequests() {
        List<BorrowRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM borrow_requests WHERE status = 'Pending' ORDER BY request_date DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BorrowRequest request = new BorrowRequest();
                request.setId(rs.getInt("id"));
                request.setBookId(rs.getInt("book_id"));
                request.setStudentRoll(rs.getString("student_roll"));
                request.setStudentName(rs.getString("student_name"));
                request.setBookTitle(rs.getString("book_title"));
                request.setStatus(rs.getString("status"));

                String dateStr = rs.getString("request_date");
                if (dateStr != null) {
                    request.setRequestDate(LocalDateTime.parse(dateStr.replace(" ", "T")));
                }

                requests.add(request);
            }

        } catch (SQLException e) {
            System.err.println("Error getting pending requests: " + e.getMessage());
            e.printStackTrace();
        }

        return requests;
    }

    public boolean approveRequest(int requestId, int adminId) {
        String updateSql = "UPDATE borrow_requests SET status = 'Approved', admin_id = ?, response_date = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setInt(1, adminId);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Error approving request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectRequest(int requestId, int adminId) {
        String updateSql = "UPDATE borrow_requests SET status = 'Rejected', admin_id = ?, response_date = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setInt(1, adminId);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Error rejecting request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public BorrowRequest getById(int id) {
        String sql = "SELECT * FROM borrow_requests WHERE id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                BorrowRequest request = new BorrowRequest();
                request.setId(rs.getInt("id"));
                request.setBookId(rs.getInt("book_id"));
                request.setStudentRoll(rs.getString("student_roll"));
                request.setStudentName(rs.getString("student_name"));
                request.setBookTitle(rs.getString("book_title"));
                request.setStatus(rs.getString("status"));

                String dateStr = rs.getString("request_date");
                if (dateStr != null) {
                    request.setRequestDate(LocalDateTime.parse(dateStr.replace(" ", "T")));
                }

                return request;
            }

        } catch (SQLException e) {
            System.err.println("Error getting request by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}

