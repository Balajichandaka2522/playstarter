package services;


import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import models.Book;
import org.bson.Document;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoOperations {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    public static MongoCollection mongoCollection;
    public static Set<Book> books = new HashSet<>();
    @Inject
    public MongoOperations() {

        mongoClient = MongoClients.create("mongodb+srv://balajichandaka:Mongoatlas25%40bal@cluster0.lvial4t.mongodb.net/");
        mongoDatabase = mongoClient.getDatabase("mocroSer"); // replace with your database name
        mongoCollection = mongoDatabase.getCollection("bookCol");
        FindIterable<Document> documents = mongoCollection.find();
        // Convert each document to a Book object and add to the set
        for (Document doc : documents) {
            String status = doc.getString("status");
            String author = doc.getString("author");
            String name = doc.getString("name");
            Integer price = doc.getInteger("price");
            String publisher=doc.getString("publisher");
            Book book = new Book(price,name,author,status,publisher);
            books.add(book);
//          RedisService.storeBook(book);
        }
    }
    public void insertDocument(Document document) {

            mongoCollection.insertOne(document);

    }
    public static Set<Book> getAllBooks(){
        return books;
    }
    public void deleteBook(Book b){
        for(Book rb:books){
            if(rb.name==b.name && rb.author==b.author && rb.status==b.status && rb.price==b.price) books.remove(rb);break;
        }
        Document filter=b.toDocument();
        mongoCollection.deleteOne(filter);
    }
    public void updateBook(Book ob,Book nb){
        mongoCollection.updateOne(ob.toDocument(),new Document("$set",nb.toDocument()));
        for(Book b:books){
            if(ob.name==b.name && ob.author==b.author && ob.status==b.status && ob.price==b.price){
                b.name=nb.name;
                b.status=nb.status;
                b.price=nb.price;
                b.author=nb.author;

            }

        }

    }

    public List<Book> sortBy(String s) {
        MongoCursor<Document> cursor;
        List<Document> aggregate=new ArrayList<>();
        Document filter=new Document("$sort",new Document(s,1));
        if (s!=null) {
            cursor = mongoCollection.aggregate(aggregate).iterator();
        } else {
            cursor = mongoCollection.find().iterator();
        }
        return docToBook(cursor);
    }
    public List<Book> docToBook(MongoCursor<Document> cursor){
        List<Book> books =new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Book book = new Book();
                book.setName(doc.getString("name"));
                book.setPrice(doc.getInteger("price"));
                book.setAuthor(doc.getString("author"));
                book.setStatus(doc.getString("status"));
                book.setPublisher(doc.getString("publisher"));
                books.add(book);
            }
        } finally {
            cursor.close();
        }
        return books;
    }
    public static Book getBook(String nm){
//        books.stream().filter(x => x.equals(nm));
        for(Book b:books){
            if(b.name.equals(nm))
                return b;
        }
        return new Book();
    }
}


