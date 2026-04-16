package dev.domain;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserMemory {

    private Map<Integer, User> users = new HashMap<>();
    private Set<Integer> loggedUsers = new HashSet<>();

    public void save(User user) {
        if (users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User " + user.getId() + " already exists");
        }
        users.put(user.getId(), user);
    }

    public User getUser(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User " + id + " not found");
        }
        return user;
    }

    public void login(int id) {
        if (isLogged(id)) {
            throw new IllegalStateException("User " + id + " is already logged in");
        }
        loggedUsers.add(id);
    }

    public void logout(int id) {
        if (!isLogged(id)) {
            throw new IllegalStateException("User " + id + " is not logged in");
        }
        loggedUsers.remove(id);
    }

    public boolean isLogged(int id) {
        return loggedUsers.contains(id);
    }

    public void delete(int id) {
        User removedUser = users.remove(id);
        if (removedUser == null) {
            throw new IllegalArgumentException("User " + id + " does not exist");
        }
        loggedUsers.remove(id);
    }
}