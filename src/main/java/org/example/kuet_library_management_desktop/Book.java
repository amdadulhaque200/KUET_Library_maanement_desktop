package org.example.kuet_library_management_desktop;

public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private String status;

    public Book(int id, String title, String author, String genre, String isbn, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getIsbn() { return isbn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
