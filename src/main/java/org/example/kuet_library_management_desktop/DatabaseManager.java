package org.example.kuet_library_management_desktop;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    roll TEXT UNIQUE NOT NULL,
                    name TEXT NOT NULL,
                    batch TEXT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    borrowed_count INTEGER DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admins (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    name TEXT NOT NULL,
                    id_number TEXT UNIQUE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create books table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    genre TEXT,
                    isbn TEXT UNIQUE,
                    status TEXT DEFAULT 'Available',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create issued_books table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS issued_books (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id INTEGER NOT NULL,
                    student_roll TEXT NOT NULL,
                    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    due_date TIMESTAMP,
                    return_date TIMESTAMP,
                    status TEXT DEFAULT 'Issued',
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (student_roll) REFERENCES students(roll)
                )
            """);

            // Create borrow_requests table for admin approval system
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS borrow_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id INTEGER NOT NULL,
                    student_roll TEXT NOT NULL,
                    student_name TEXT NOT NULL,
                    book_title TEXT NOT NULL,
                    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status TEXT DEFAULT 'Pending',
                    admin_id INTEGER,
                    response_date TIMESTAMP,
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (student_roll) REFERENCES students(roll),
                    FOREIGN KEY (admin_id) REFERENCES admins(id)
                )
            """);

            // Insert sample books if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("""
                    INSERT INTO books (title, author, genre, isbn) VALUES
                    ('Java Programming', 'Herbert Schildt', 'Programming', 'ISBN-001'),
                    ('Data Structures and Algorithms', 'Narasimha Karumanchi', 'Computer Science', 'ISBN-002'),
                    ('Introduction to Algorithms', 'CLRS', 'Computer Science', 'ISBN-003'),
                    ('Clean Code', 'Robert C. Martin', 'Programming', 'ISBN-004'),
                    ('Design Patterns', 'Gang of Four', 'Software Engineering', 'ISBN-005')
                """);
            }

            System.out.println("✓ Database initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        instance = null;
    }
}

