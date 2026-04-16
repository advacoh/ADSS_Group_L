package dev.domain;


public class UserController {

    private UserMemory userMemory;

    public UserController(UserMemory userMemory) {
        this.userMemory = userMemory;
    }

    public boolean isLogged(int id) {
        try {
            userMemory.getUser(id);
            return userMemory.isLogged(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("isLogged failed: " + e.getMessage());
        }
    }

    public void register(int id, String password) {
        try {
            User user = new User(id, password);
            userMemory.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        }
    }

    public void delete(int id) {
        try {
            userMemory.delete(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Delete failed: " + e.getMessage());
        }
    }

    public void login(int id, String password) {
        try {
            User user = userMemory.getUser(id);
            if (!user.login(password)) {
                throw new IllegalArgumentException("Incorrect password");
            }
            userMemory.login(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Login failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Login failed: " + e.getMessage());
        }
    }

    public void logout(int id) {
        try {
            userMemory.logout(id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Logout failed: " + e.getMessage());
        }
    }
}
