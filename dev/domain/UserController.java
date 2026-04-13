package dev.domain;


public class UserController {
    private UserMemory userMemory;

    public UserController(UserMemory userMemory) {
        this.userMemory = userMemory;
    }

    public boolean isLogged(int id) {
        userMemory.getUser(id); // ensure user exists
        return userMemory.isLogged(id);
    }

    public void register(int id, String password) {
        User user = new User(id, password);
        userMemory.save(user);
    }

    public void delete(int id) {
        userMemory.getUser(id); //throws if user doesnt exist
        userMemory.delete(id);
    }

    public void login(int id, String password) {
        if (isLogged(id)) {
            throw new IllegalStateException("User already logged in");
        }
        User user = userMemory.getUser(id);
        if (!user.login(password)) {
            throw new IllegalArgumentException("Incorrect password");
        }

        userMemory.login(id);
    }
    public void logout(int id) {
        if (!isLogged(id)) {
            throw new IllegalStateException("User is not logged in");
        }
        userMemory.logout(id);
    }
}
