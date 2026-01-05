package org.example.kuet_library_management_desktop;

public class Student {
    private int id;
    private String roll;
    private String name;
    private String batch;
    private String email;
    private String password;
    private int borrowedCount;

    public Student() {
    }

    public Student(int id, String roll, String name, String batch, String email, int borrowedCount) {
        this.id = id;
        this.roll = roll;
        this.name = name;
        this.batch = batch;
        this.email = email;
        this.borrowedCount = borrowedCount;
    }

    public Student(String roll, String name, String email, int borrowedCount) {
        this.roll = roll;
        this.name = name;
        this.email = email;
        this.borrowedCount = borrowedCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoll() { return roll; }
    public void setRoll(String roll) { this.roll = roll; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    public int getBorrowedCount() { return borrowedCount; }
    public void setBorrowedCount(int borrowedCount) { this.borrowedCount = borrowedCount; }
}

