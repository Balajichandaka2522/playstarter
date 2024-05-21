package daos;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.typesafe.config.Config;
import models.User;
import org.bson.types.ObjectId;
import org.mongodb.scala.*;
import org.mongodb.scala.model.Filters;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class UserDAO {
    private final MongoCollection<User> userCollection;

    @Inject
    public UserDAO(Config config) {
        String mongoUri = config.getString("mongodb.uri");
        MongoClient mongoClient = MongoClients.create(mongoUri);
        MongoDatabase database = mongoClient.getDatabase("mydatabase");
        userCollection = database.getCollection("users", User.class);
    }

    public CompletableFuture<User> findById(ObjectId id) {
        return userCollection.find(Filters.eq("_id", id)).first().toFuture();
    }

    public CompletableFuture<List<User>> findAll() {
        return userCollection.find().toFuture().thenApply(users -> {
            List<User> userList = new ArrayList<>();
            users.forEach(userList::add);
            return userList;
        });
    }

    public CompletableFuture<InsertOneResult> save(User user) {
        return userCollection.insertOne(user).toFuture();
    }

    public CompletableFuture<UpdateResult> update(User user) {
        return userCollection.replaceOne(Filters.eq("_id", user.getId()), user).toFuture();
    }

    public CompletableFuture<DeleteResult> delete(ObjectId id) {
        return userCollection.deleteOne(Filters.eq("_id", id)).toFuture();
    }
}
