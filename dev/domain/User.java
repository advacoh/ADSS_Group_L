package dev.domain;

public class User {
    protected int ID; 
    private String password;

    public boolean login(String pass) { return false; }
    private boolean isValidPassword(String pass) { return false; }
    private boolean isValidUserName(int ID) { return false; }
}