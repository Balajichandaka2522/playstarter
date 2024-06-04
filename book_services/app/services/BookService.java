package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import models.Book;
import org.bson.Document;
import play.libs.ws.*;
import play.libs.Json;
import play.Configuration;

import javax.inject.Inject;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BookService {
    MongoOperations mongoOperations = new MongoOperations();
    private final WSClient ws;
    public Configuration config;
    @Inject
    public BookService(WSClient ws,Configuration config ) {
        this.ws = ws;
        this.config=config;
    }

    public static Set<Book> getAllBooks(){
        return MongoOperations.getAllBooks();
    }
    public void createBook(Book b){
        MongoOperations.books.add(b);
        mongoOperations.insertDocument(b.toDocument());
    }
    public void deleteBook(Book b){
        MongoOperations.books.remove(b);
//        mongoCollection.deleteOne(b.toDocument());
    }
    public void upBook(Book ob,Book nb) throws JsonProcessingException {
        mongoOperations.updateBook(ob,nb);
        RedisService.updateBook(ob,nb);
    }
    public Book getBook(Book rb){
        int f=0;Book t=new Book();
        for(Book b:MongoOperations.books){
            if(rb.name==b.name && rb.author==b.author && rb.status==b.status && rb.price==b.price) f=1;t=b ;
            if(rb.name==b.name && rb.author==b.author && rb.status==b.status && rb.price==b.price) f=1;t=b ;
        }
        if(f==0){
            return null;
        }
        else return t;
    }
public void dBook(Book b){

        mongoOperations.deleteBook(b);
        RedisService.deleteBook(b.name);
}
public List<Book> sortBy(String s) {

    return mongoOperations.sortBy(s);
}
    public CompletionStage<JsonNode> getAuth(JsonNode autNode) {
//        String authorServiceUrl= config.getString("con.authorurl");
//        String authorServiceUrl = "http://localhost:9001/author" ;
//        ObjectNode aut=new ObjectMapper().createObjectNode();
//        aut.put("author",name);
        return ws.url(config.getString("con.authorurl"))
                .post(autNode)
                .thenApply(response -> {
                    if (response.getStatus() == 200) {
                        return response.asJson();
                    } else {
                        throw new RuntimeException("Failed to fetch author details, status: " + response.getStatus());
                    }
                }).exceptionally(throwable -> {
                    throw new RuntimeException("Exception occurred while fetching author details", throwable);
                });
    }
//using CompletionStage and thenApply method
//    public CompletionStage<JsonNode> getPub(String name) {
////        String authorServiceUrl = "http://localhost:9001/publisher" ;
//        String authorServiceUrl= config.getString("con.publisherurl");
//        ObjectNode pub=new ObjectMapper().createObjectNode();
//        pub.put("publisher",name);
//        return ws.url(authorServiceUrl)
//                .post(pub)
//                .thenApply(response -> {
//                    if (response.getStatus() == 200) {
//                        return response.asJson();
//                    } else {
//                        throw new RuntimeException("Failed to fetch author details, status: " + response.getStatus());
//                    }
//                }).exceptionally(throwable -> {
//                    throw new RuntimeException("Exception occurred while fetching publisher details", throwable);
//                });
//    }

    public CompletionStage<JsonNode> getPub(JsonNode pubNode) {
        // Using CompletableFuture.supplyAsync to perform the asynchronous operation
        CompletableFuture<JsonNode> future = CompletableFuture.supplyAsync(() -> {
            // Simulate fetching data from config or external service
//            String authorServiceUrl = config.getString("con.publisherurl");
//            ObjectNode pub = new ObjectMapper().createObjectNode();
//            pub.put("publisher", name);
            // Perform the HTTP POST request and handle the response
            return ws.url(config.getString("con.publisherurl"))
                    .post(pubNode)
                    .thenApply(response -> {
                        if (response.getStatus() == 200) {
                            return response.asJson();
                        } else {
                            throw new RuntimeException("Failed to fetch author details, status: " + response.getStatus());
                        }
                    }).exceptionally(throwable -> {
                        throw new RuntimeException("Exception occurred while fetching publisher details", throwable);
                    }).toCompletableFuture().join();});

        // Return the CompletableFuture as a CompletionStage
        return future;
    }

   public CompletionStage<JsonNode> getBook(String nm){
       Book book = mongoOperations.getBook(nm);
       String authorName = book.author;
//       String authorsServiceUrl = "http://localhost:9001/author";
       String authorsServiceUrl= config.getString("con.authorurl");
       String publisherName = book.publisher;
//       String publishersServiceUrl = "http://localhost:9001/publisher";
       String publishersServiceUrl= config.getString("con.publisherurl");

       ObjectNode autNode=new ObjectMapper().createObjectNode();
       autNode.put("author",book.author);
       ObjectNode pubNode=new ObjectMapper().createObjectNode();
       pubNode.put("publisher",book.publisher);
//        ObjectNode pubNode=Json.newObject();
//        autNode.put("author",book.author);
//        pubNode.put("publisher",book.publisher);
       // Create completion stages for fetching author and publisher details
       CompletableFuture<JsonNode> authorDetailsStage = ws.url(authorsServiceUrl).post(autNode).thenApply(response -> {
           if (response.getStatus() == 200) {
               return response.asJson();
           } else {
               throw new RuntimeException("Failed to fetch author details");
           }
       }).toCompletableFuture();

       CompletableFuture<JsonNode> publisherDetailsStage = ws.url(publishersServiceUrl).post(pubNode).thenApply(response -> {
           if (response.getStatus() == 200) {
               return response.asJson();
           } else {
               throw new RuntimeException("Failed to fetch publisher details");
           }
       }).toCompletableFuture();

       // Combine author and publisher details completion stages using CompletableFuture
       CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(
               authorDetailsStage, publisherDetailsStage);

       // Handle the completion of combined CompletableFuture
       return combinedFuture.thenApplyAsync(ignored -> {
           // Get author and publisher details
           JsonNode authorDetails = authorDetailsStage.join();
           JsonNode publisherDetails = publisherDetailsStage.join();

           // Combine book details and author/publisher details into a single JSON response
           ObjectNode bookDetails = Json.newObject();
           bookDetails.set("book", Json.toJson(book));
           bookDetails.set("authorDetails", authorDetails);
           bookDetails.set("publisherDetails", publisherDetails);

           return bookDetails;
       });

   }
    public CompletionStage<String> fetchDataFromPostRequest(JsonNode jsonData) {
        return ws.url("http://localhost:9001/data")
                .setContentType("application/json")
                .post(jsonData)
                .thenApply(response -> {
                    // Process the response body
                    JsonNode responseData = response.asJson();
                    // Extract and process the data as needed
                    // For example:
                    String message = responseData.get("msg").asText();
                    System.out.println("Received message: " + message);
                    return message;
                });


    }




}

