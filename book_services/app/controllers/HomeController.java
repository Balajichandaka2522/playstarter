package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Book;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.*;
import services.BookService;
import play.libs.ws.*;
import services.RedisService;
import services.TestRedis;


import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

import static services.RedisService.objectMapper;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    public BookService bookService;
    public Book updateob;
    private final WSClient ws;
    public RedisService redisService;

    @Inject
    FormFactory formFactory;
    @Inject
    public HomeController(BookService bookService,WSClient ws,RedisService redisService){
        this.bookService=bookService;
        this.ws=ws;
        this.redisService=redisService;
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>*/
    public Result index() {
        return ok(Json.toJson(BookService.getAllBooks()));}
    public Result create(){
        Form<Book> bf= formFactory.form(Book.class);
        return ok(views.html.book.create.render(bf));
    }
    public Result save(){
        JsonNode bookNode=request().body().asJson();
        Book newb=new Book(bookNode.get("price").asInt(),bookNode.get("name").asText(),bookNode.get("author").asText(),bookNode.get("status").asText(),bookNode.get("publisher").asText());
        bookService.createBook(newb);
        redisService.storeBook(newb);
        return redirect("/");
    }
    public Result show(){
        Map<String, String[]> json = request().body().asFormUrlEncoded();
        if (json == null) {
            return badRequest("Expecting JSON data");
        }
        Book book=new Book(Integer.valueOf(json.get("price")[0]),json.get("name")[0],json.get("author")[0],json.get("status")[0],json.get("publisher")[0]);
        // Process the book object
        return ok(views.html.book.show.render(book));
    }
    public Result edit(Integer price,String name,String status,String author,String pub){
        Book bk=new Book(price,name,author,status,pub);
        Form<Book> editBook=formFactory.form(Book.class).fill(bk);
        updateob=bk;
        return ok(views.html.book.edit.render(editBook));
    }
    public Result update() throws IOException {
        JsonNode bookJson= request().body().asJson();
        Book book = objectMapper.readValue(bookJson.toString(), Book.class);
        bookService.upBook(redisService.getBook(book.name),book);
        return ok(views.html.book.show.render(book));
    }
    public Result destroy(Integer price,String name,String status,String author,String pub){

        Book dbook= new Book(price,name,author,status,pub);
        bookService.dBook(dbook);
        return redirect("/");
    }

    public Result sort(String s){

        List<Book> books=bookService.sortBy(s);
        return ok(Json.toJson(books));
    }


    public CompletionStage<Result> getAut(String name){
//        redisService.getAut(name);
        return bookService.getAuth(Json.newObject().put("author",name)).thenApply(jsonNode -> {
            return ok(jsonNode);

        }).exceptionally(throwable -> {
            return internalServerError(throwable.getMessage());
        });

    }

    public CompletionStage<Result> getPub(String name){
//        redisService.getPub(name);
        return bookService.getPub(Json.newObject().put("publisher",name)).thenApply(json->{return ok(json);}).exceptionally(throwable -> {return internalServerError(throwable.getMessage());});
    }

//    public Result getBook(String name){
//        Book b=bookService.getBook(name);
//        // implements WSBodyReadables or use WSBodyReadables.instance.json()
//        CompletionStage<JsonNode> jsonPromise = ws.url(url).get().thenApply(r -> r.getBody(json()));
//        JsonNode auth=  bookService.getAuth(b.author).thenApply(json->{return json;});
//        JsonNode pub=  bookService.getPub(b.publisher).thenApply(jsonNode -> {return jsonNode;});
//
////        JsonNode bn=Json.toJson(b);
//        ObjectNode jsonNode = Json.newObject();
//
//        // Add fields to the ObjectNode
//        jsonNode.put("name",b.name);
//        jsonNode.put("author",auth.get("author"));
//        jsonNode.put("publisher", pub.get("publisher"));
//
//        // Convert ObjectNode to JsonNode (if necessary, ObjectNode is already a JsonNode)
//        JsonNode finalJsonNode = jsonNode;
//
//        return ok(finalJsonNode);
//    }



//    public CompletionStage<Result> getBook(String name) {
//
//        Book book = bookService.getBook(name);
//        String authorName = book.author;
//        String authorsServiceUrl = config.getConfig("authorurl")+ authorName.replace(" ","%20");
//        String publisherName=book.publisher;
//        String publishersServiceUrl = config.getConfig("publisherurl") + publisherName.replace(" ","%20");
//
//        return ws.url(authorsServiceUrl).get().thenCompose(response -> {
//            if (response.getStatus() == 200) {
//                JsonNode authorDetails = response.asJson();
//
//                // Combine book details and author details into a single JSON response
//                JsonNode bookDetails = Json.toJson(book);
//                ((ObjectNode) bookDetails).set("authorDetails", authorDetails);
//
//                return ws.url(publishersServiceUrl).get().thenApply(res->{
//                    if(res.getStatus()==200){
//                        JsonNode publisherDetails=res.asJson();
//
//                        ((ObjectNode) bookDetails).set("publisherDetails",publisherDetails);
//                        return ok(bookDetails);
//                    }
//                    else{
//                        return internalServerError("Failed to fetch publisher details");
//                    }
//                });
//
//            } else {
//
//                return CompletableFuture.completedFuture(internalServerError("Failed to fetch author details"));
//            }
//        });
//    }


    public CompletionStage<Result> getBook(String name) {
//        return bookService.getBook(name).thenApply(r->{return ok(r);});
//        Book book = bookService.getBook(name);
        Book book=redisService.getBook(name);
        String authorName = book.author;
        String authorsServiceUrl = "http://localhost:9001/author";
        String publisherName = book.publisher;
        String publishersServiceUrl = "http://localhost:9001/publisher";
        ObjectNode autNode=new ObjectMapper().createObjectNode();
        autNode.put("author",book.author);
        ObjectNode pubNode=new ObjectMapper().createObjectNode();
        pubNode.put("publisher",book.publisher);
//        ObjectNode pubNode=Json.newObject();
//        autNode.put("author",book.author);
//        pubNode.put("publisher",book.publisher);

        // Create completion stages for fetching author and publisher details
        CompletionStage<JsonNode> authorDetailsStage = ws.url(authorsServiceUrl).post(autNode).thenApply(response -> {
            if (response.getStatus() == 200) {
                return response.asJson();
            } else {
                throw new RuntimeException("Failed to fetch author details");
            }
        }).toCompletableFuture();

        CompletionStage<JsonNode> publisherDetailsStage = ws.url(publishersServiceUrl).post(pubNode).thenApply(response -> {
            if (response.getStatus() == 200) {
                return response.asJson();
            } else {
                throw new RuntimeException("Failed to fetch publisher details");
            }
        }).toCompletableFuture();

        // Combine author and publisher details completion stages using CompletableFuture
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(
                authorDetailsStage.toCompletableFuture(), publisherDetailsStage.toCompletableFuture());

        // Handle the completion of combined CompletableFuture
        return combinedFuture.thenApplyAsync(ignored -> {
            // Get author and publisher details
            JsonNode authorDetails = authorDetailsStage.toCompletableFuture().join();
            JsonNode publisherDetails = publisherDetailsStage.toCompletableFuture().join();

            // Combine book details and author/publisher details into a single JSON response
            ObjectNode bookDetails = Json.newObject();
            bookDetails.set("book", Json.toJson(book));
            bookDetails.set("authorDetails", authorDetails);
            bookDetails.set("publisherDetails", publisherDetails);

            return ok(bookDetails);
        }).exceptionally(ex -> {
            // Handle exceptions
            if (ex.getCause() instanceof RuntimeException) {
                return internalServerError(ex.getCause().getMessage());
            } else {
                return internalServerError("An unexpected error occurred");
            }
        });
    }

    public CompletionStage<Result> fetchDataFromPostRequest() {
        // Prepare the JSON payload
        JsonNode jsonData = request().body().asJson();
        return bookService.fetchDataFromPostRequest(jsonData).thenApply(m->{return ok(m);});

    }
//
//    public Result getBook(String name){
//        return ok(Json.toJson(redisService.getBook(name)));
//
//    }
    public Result rkeys(){
        return ok(Json.toJson(redisService.getKeys()));
    }

    public Result setV(){
        JsonNode json=request().body().asJson();
        redisService.set(json.get("key").asText(),json.get("value").asText());
        return ok("values set");


    }
    public  Result getrBooks(){
        return ok(Json.toJson(redisService.getBooks()));
    }
    public Result deleterBook(){
        JsonNode bookJson=request().body().asJson();
         bookService.dBook(redisService.getBook(bookJson.get("name").asText()));
        return ok("book deleted successfully");
    }




}
