package domain.hr;
public class User {

    protected int ID;
    private String password;

    public User(int ID, String password) {
        try {
            if (!isValidID(ID)) {
                throw new IllegalArgumentException("Invalid ID: must be a 9-digit number");
            }
            if (!isValidPassword(password)) {
                throw new IllegalArgumentException("Invalid password: must be at least 6 characters");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User creation failed: " + e.getMessage());
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
        return pass != null && pass.length() >= 6;
    }

    private boolean isValidID(int ID) {
        return ID >= 100000000 && ID <= 999999999;
    }
}