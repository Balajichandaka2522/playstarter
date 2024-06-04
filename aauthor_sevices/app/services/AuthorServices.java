package services;


import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.*;
import models.Author;
import models.Book;
import models.Publisher;
import org.bson.Document;
import play.libs.Json;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AuthorServices {
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    public static MongoCollection mongoCollection;
    public static MongoCollection mongoPubCollection;
    @Inject
    public AuthorServices() {
        mongoClient = MongoClients.create("mongodb+srv://balajichandaka:Mongoatlas25%40bal@cluster0.lvial4t.mongodb.net/");
        mongoDatabase = mongoClient.getDatabase("mocroSer"); // replace with your database name
        mongoCollection = mongoDatabase.getCollection("autCol");
        mongoPubCollection=mongoDatabase.getCollection("publishers");
        FindIterable<Document> documents = mongoCollection.find();
        // Convert each document to a Book object and add to the set
//        for (Document doc : documents) {
//            String status = doc.getString("status");
//            String author = doc.getString("author");
//            String name = doc.getString("name");
//            Integer price = doc.getInteger("price");
//            Book book = new Book(price,name,author,status);
//            books.add(book);
//        }
    }
    public static Author getAuthor(String authorName){
        Document query = new Document("author", authorName);
        Document authorDoc = (Document) mongoCollection.find(query).first();
        if (authorDoc != null) {
            String author = authorDoc.getString("author");
            List<Document> bookDocs = (List<Document>) authorDoc.get("books");
            List<Book> books = new ArrayList<>();

            for (Document bookDoc : bookDocs) {
                String name = bookDoc.getString("name");
                Integer price = bookDoc.getInteger("price");
                String status = bookDoc.getString("status");
                books.add(new Book(price,name,  status));
            }

            return new Author(author, books);
        } else {
            List<Book> books = new ArrayList<>();
            mongoCollection.insertOne(new Document("author",authorName).append("books",books));
            return new Author(authorName,books); // Author not found
        }

    }

    public Publisher getPub(String name){
        Document query = new Document("publisher", name);
        Document pubDoc = (Document) mongoPubCollection.find(query).first();
        if (pubDoc != null) {
            String publisher = pubDoc.getString("publisher");
            String mail=pubDoc.getString("email");
            return new Publisher(publisher,mail);
        }
        else {
            String mail="yet to added";
            mongoPubCollection.insertOne(new Document("publisher",name).append("email",mail));
            return new Publisher(name,mail);        }


    }
    public List<Author> getAuthors(){
            List<Author> autDocs = new ArrayList<>();

            try (MongoCursor<Author> cursor = mongoCollection.find().iterator()) {
                while (cursor.hasNext()) {
                    autDocs.add(cursor.next());
                }
            }
            return autDocs;
        }

        public void sendResponse(JsonNode json){




        }


}


