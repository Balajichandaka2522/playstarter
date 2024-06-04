package models;

import java.util.List;

public class Author {
    public String author;
    public List<Book> books;

    public Author(String author, List<Book> books) {
        this.author=author;
        this.books=books;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }


}
