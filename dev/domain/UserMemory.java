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
            throw new IllegalArgumentException("User already exists");
        }

        users.put(user.getId(), user);
    }

    public User getUser(int id) {

        User user = users.get(id);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return user;
    }


    public void login(int id) {

        if (isLogged(id)) {
            throw new IllegalStateException("User already logged in");
        }

        loggedUsers.add(id);
    }


    public void logout(int id) {

        if (!isLogged(id)) {
            throw new IllegalStateException("User is not logged in");
        }

        loggedUsers.remove(id);
    }


    public boolean isLogged(int id) {
        return loggedUsers.contains(id);
    }

    public void delete(int id) {
        users.remove(id);
        loggedUsers.remove(id); // safe even if not logged
    }
}