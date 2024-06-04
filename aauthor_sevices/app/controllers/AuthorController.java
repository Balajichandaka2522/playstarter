package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Result;
import services.AuthorServices;

import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class AuthorController {
public AuthorServices authorServices=new AuthorServices();
    public Result getAuthor(){
    JsonNode aNode=request().body().asJson();
        return ok(Json.toJson(AuthorServices.getAuthor(aNode.get("author").asText())));
    }
    public Result getPublisher(){
        JsonNode pNode=request().body().asJson();
        return ok(Json.toJson(authorServices.getPub(pNode.get("publisher").asText())));
    }

    public Result getAuthors(){
        return ok(Json.toJson(authorServices.getAuthors()));
    }
    public Result processJsonRequest() {
        JsonNode jsonData = request().body().asJson();

        if (jsonData == null) {
            return badRequest("Expecting JSON data");
        }

        // Now you can process the JSON data as needed
        String name = jsonData.get("name").asText();
        int age = jsonData.get("age").asInt();

        // Example: Printing the extracted data
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        String ms1="hi "+name+",your data recieved";
        ObjectNode ms=new ObjectMapper().createObjectNode();
        ms.put("msg",ms1);

        // Return a response (e.g., OK)
        return ok(ms);
    }
    }
