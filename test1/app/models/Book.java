package models;

import org.bson.types.ObjectId;

import java.util.HashSet;

public class Book {
    public Integer id;
    public String title;
    public String author;
    public Integer price;
    public Book(){

    }
    public Book(Integer id,Integer price,String author,String title){
        this.id=id;
        this.title=title;
        this.author=author;
        this.price=price;
    }
    public static HashSet<Book> books;
    static {
        books =new HashSet<>();
        books.add(new Book(1,  2000,"daniel","history of violence"));
        books.add(new Book(2,  3000,"emanuel","leo"));
    }
    public static HashSet<Book> allBooks(){
        return books;
    }
    public static void add(Book book){
        books.add(book);
    }
    public static boolean remove(Book book){
        return books.remove(book);
    }
    public static Book findById(Integer id){
        for(Book book:books){
            if(id.equals(book.id)){
                return book;
            }
        }
        return null;
    }

}
