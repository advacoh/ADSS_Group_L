package dataAccess.hr;

public class UserDTO {
    private final int id;
    private final String password;
    private final boolean isLoggedIn; 

    public UserDTO(int id, String password, boolean isLoggedIn) {
        this.id = id;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }

    public int getId() { 
        return id; 
    }
    
    public String getPassword() { 
        return password; 
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
