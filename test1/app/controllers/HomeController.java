package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.MongoService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.inject.Inject;

public class HomeController extends Controller {

    private final MongoService mongoService;

    @Inject
    public HomeController(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    public Result index() {
        MongoDatabase database = mongoService.getDatabase();
        MongoCollection<Document> collection = database.getCollection("col1");

        // Example: Insert a document
        Document doc = new Document("name", "John Donald").append("age", 30);
        collection.insertOne(doc);

        return ok("Document inserted!");
    }
}
