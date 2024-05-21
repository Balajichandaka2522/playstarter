package services;

import daos.UserDAO;
import models.User;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private final UserDAO userDAO;

    @Inject
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public CompletableFuture<User> findUserById(ObjectId id) {
        return userDAO.findById(id);
    }

    public CompletableFuture<List<User>> getAllUsers() {
        return userDAO.findAll();
    }

    public CompletableFuture<Void> createUser(User user) {
        return userDAO.save(user).thenAccept(result -> {});
    }

    public CompletableFuture<Void> updateUser(User user) {
        return userDAO.update(user).thenAccept(result -> {});
    }

    public CompletableFuture<Void> deleteUser(ObjectId id) {
        return userDAO.delete(id).thenAccept(result -> {});
    }
}
