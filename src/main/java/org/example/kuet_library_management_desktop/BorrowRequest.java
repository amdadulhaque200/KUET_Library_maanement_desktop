package org.example.kuet_library_management_desktop;

import java.time.LocalDateTime;

public class BorrowRequest {
    private int id;
    private int bookId;
    private String studentRoll;
    private String studentName;
    private String bookTitle;
    private LocalDateTime requestDate;
    private String status; // Pending, Approved, Rejected
    private Integer adminId;
    private LocalDateTime responseDate;

    public BorrowRequest() {
    }

    public BorrowRequest(int bookId, String studentRoll, String studentName, String bookTitle) {
        this.bookId = bookId;
        this.studentRoll = studentRoll;
        this.studentName = studentName;
        this.bookTitle = bookTitle;
        this.status = "Pending";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }
}

