package services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import play.api.Configuration;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class MongoService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    @Inject
    public MongoService(Configuration configuration, ApplicationLifecycle lifecycle) {
        String uri = "mongodb://localhost:27017";
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("balajimdb");

        // Registering a shutdown hook to close the MongoClient
        lifecycle.addStopHook(() -> {
            mongoClient.close();
            return CompletableFuture.completedFuture(null);
        });
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
