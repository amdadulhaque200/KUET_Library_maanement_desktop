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

            // Create students table (using email instead of username)
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

            // Create admins table (using email instead of username)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admins (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    name TEXT NOT NULL,
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

            // Insert default admin if not exists
            String checkAdmin = "SELECT COUNT(*) FROM admins WHERE email = 'admin@kuet.ac.bd'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("""
                    INSERT INTO admins (email, password, name) 
                    VALUES ('admin@kuet.ac.bd', 'admin123', 'System Administrator')
                """);
            }

            // Insert sample books if table is empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
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
            System.out.println("✓ Default admin: admin@kuet.ac.bd / admin123");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        // SQLite doesn't require explicit connection pool management
        instance = null;
    }
}

