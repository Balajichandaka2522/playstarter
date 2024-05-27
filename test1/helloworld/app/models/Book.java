package models;

import java.util.HashSet;
import java.util.Set;


public class Book {
    public Integer id;
    public String title;
    public Integer price;
    public String author;

    public Book(){}

    public Book(Integer id,String title,Integer price,String author){
        this.id=id;
        this.title=title;
        this.price=price;
        this.author=author;
    }
    private static Set<Book> books;
    static{
        books= new HashSet<>();
        books.add(new Book(1,"C++",500,"ABC"));
        books.add(new Book(2,"Python",150,"XYZ"));
        books.add(new Book(3,"Java",200,"QWE"));
    }

    public static Set<Book> allBooks(){
        return books;
    }

    public static Book findById(Integer id){
        for(Book b:books){
            if(id.equals(b.id)){
                return b;
            }
        }
        return null;
    }

    public static void add(Book b){
        books.add(b);
    }

    public static boolean remove(Book b){
        return books.remove(b);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
