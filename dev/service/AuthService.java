package dev.service;
import dev.domain.UserController;

public class AuthService {
    private UserController userController;

    public AuthService( UserController userController){
        this.userController = userController;
    }

    public boolean login(int id, String pass) { return false; }
    public boolean logout(int id) { return false; }
}