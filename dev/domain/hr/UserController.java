package domain.hr;
import dataAccess.hr.UserMapper;
import dataAccess.hr.UserDTO;
import java.util.HashSet;
import java.util.Set;


public class UserController {

    private final UserMapper userMapper;

    public UserController() {
        this.userMapper = new UserMapper();
    }

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean isLogged(int id) {
        UserDTO dto = userMapper.selectById(id);
        if (dto == null) {
            throw new IllegalArgumentException("isLogged failed: User " + id + " not found");
        }
        return dto.isLoggedIn(); 
    }


    public void register(int id, String password) {
        try {
            // If id or password break any rules, the user constructor throws an exception.
            new User(id, password);
            UserDTO dto = new UserDTO(id, password, false);
            if (!userMapper.insert(dto)) {
                throw new IllegalArgumentException("User " + id + " already exists in database");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Registration failed due to a database error.");
        }
    }


    public void delete(int id) {
        try {
            // Attempt to execute the deletion.
            if (!userMapper.delete(id)) {
                throw new IllegalArgumentException("User " + id + " does not exist");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Delete failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Delete failed due to a database error.");
        }
    }


    public void login(int id, String typedPassword) { 
        try {
            UserDTO dto = userMapper.selectById(id);
            if (dto == null) {
                throw new IllegalArgumentException("User " + id + " not found");
            }
            User domainUser = new User(dto.getId(), dto.getPassword());
            if (!domainUser.login(typedPassword)) {
                throw new IllegalArgumentException("Incorrect password");
            }
            if (dto.isLoggedIn()) {
                throw new IllegalStateException("User " + id + " is already logged in");
            }
            if (!userMapper.updateLoginStatus(id, true)) {
                throw new RuntimeException("Could not update login status in database.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Login failed due to an unexpected database error.");
        }
    }

    public void logout(int id) {
        try {
            UserDTO dto = userMapper.selectById(id);
            if (dto == null || !dto.isLoggedIn()) {
                throw new IllegalStateException("User " + id + " is not logged in");
            }
            if (!userMapper.updateLoginStatus(id, false)) {
                throw new RuntimeException("Could not update logout status in database.");
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Logout failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Logout failed due to an unexpected database error.");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Invalid password: must be at least 6 characters");
        }
    }
}
