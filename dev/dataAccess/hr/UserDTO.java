package dataAccess.hr;

public class UserDTO {
    private final int id;
    private final String password;

    public UserDTO(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public int getId() { 
        return id; 
    }
    
    public String getPassword() { 
        return password; 
    }
}
