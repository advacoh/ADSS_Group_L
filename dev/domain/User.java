package dev.domain;

public class User {
    protected String ID; 
    private String password;

    public boolean login(String pass) { return false; }
    private boolean isValidPassword(String pass) { return false; }
    private boolean isValidUserName(String ID) { return false; }
}