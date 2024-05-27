package services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Book;
import org.bson.Document;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoBookService {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    public Set<Book> books;

    @Inject
    public MongoBookService() {
        // Connect to MongoDB
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("balajimdb"); // replace with your database name
        collection = database.getCollection("col1"); // replace with your collection name
    }
    public Set<Book> getAllBooks(){

        books = new HashSet<>();
        for (Document doc : collection.find()) {
            Book book = new Book();
            book.title = doc.getString("title");
            book.author = doc.getString("author");
            book.price = doc.getInteger("price");
            book.id = doc.getInteger("id");
            books.add(book);
        }
        return books;

    }
    public Book findById(Integer id){
        for(Book b:books){
            if(id.equals(b.id))
                return b;
        }
        return null;

    }
    public void remove(Integer id){

        books.removeIf(b -> id.equals(b.id));
        this.updateCol();

    }
    public void updateBook(Integer id,Book ub){
        for(Book b:books){
            if(id.equals(b.id)){
                b.id=ub.id;
                b.author=ub.author;
                b.price=ub.price;
                b.title=ub.title;

            }

        }
        this.updateCol();
    }
    public void updateCol(){
        collection.deleteMany(new Document());
        List<Document> documents = books.stream()
                .map(book -> new Document("title", book.title)
                        .append("author", book.author)
                        .append("price", book.price)
                        .append("id", book.id))
                .collect(Collectors.toList());
        collection.insertMany(documents);
    }


}
