package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class UserController extends Controller {
    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public CompletionStage<Result> getUser(String id) {
        return userService.findUserById(new ObjectId(id))
                .thenApply(user -> user == null ? notFound("User not found") : ok(Json.toJson(user)));
    }

    public CompletionStage<Result> getAllUsers() {
        return userService.getAllUsers().thenApply(users -> ok(Json.toJson(users)));
    }

    public CompletionStage<Result> createUser() {
        JsonNode json = request().body().asJson();
        User user = Json.fromJson(json, User.class);
        return userService.createUser(user).thenApply(result -> created(Json.toJson(user)));
    }

    public CompletionStage<Result> updateUser(String id) {
        JsonNode json = request().body().asJson();
        User user = Json.fromJson(json, User.class);
        user.setId(new ObjectId(id));
        return userService.updateUser(user).thenApply(result -> ok(Json.toJson(user)));
    }

    public CompletionStage<Result> deleteUser(String id) {
        return userService.deleteUser(new ObjectId(id)).thenApply(result -> ok("User deleted"));
    }
}
