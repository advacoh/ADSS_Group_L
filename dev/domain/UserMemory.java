package dev.domain;
import java.util.Map;

public class UserMemory {
    private Map<Integer, User> users;

    public boolean save(User user) { return false; }
    public User get(int id) { return null; }
    public boolean delete(int id) { return false; }
}