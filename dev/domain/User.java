package dev.domain;
public class User {
    protected int ID; 
    private String password;

    public User(int ID, String password) {
        if (!isValidID(ID)) {
            throw new IllegalArgumentException("Invalid ID");
        }

        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        this.ID = ID;
        this.password = password;
    }

    public int getId() {
        return ID;
    }

    public boolean login(String pass) {
        return this.password.equals(pass);
    }

    private boolean isValidPassword(String pass) {
         if (pass == null) {
             return false;
         }
         return pass.length() >= 6;
    }

    private boolean isValidID(int ID) {
        return ID >= 100000000 && ID <= 999999999;
    }
}