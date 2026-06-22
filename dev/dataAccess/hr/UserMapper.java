package dataAccess.hr;

import java.io.File;
import java.sql.*;

public class UserMapper {
    private static final String TABLE_NAME = "Users";
    private final String connectionString;
    private boolean didCreateTable = false;

    public UserMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    public boolean insert(UserDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME + " (ID, Password, IsLoggedIn) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dto.getId());
            stmt.setString(2, dto.getPassword());
            stmt.setInt(3, dto.isLoggedIn() ? 1 : 0);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during insert: " + e.getMessage());
        }
    }

    public UserDTO selectById(int id) {
        String sql = "SELECT ID, Password, IsLoggedIn FROM " + TABLE_NAME + " WHERE ID = ?;";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
                        rs.getInt("ID"), 
                        rs.getString("Password"), 
                        rs.getInt("IsLoggedIn") == 1
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during lookup: " + e.getMessage());
        }
        return null;
    }

    public boolean updateLoginStatus(int id, boolean logState) {
        String sql = "UPDATE " + TABLE_NAME + " SET IsLoggedIn = ? WHERE ID = ?;";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, logState ? 1 : 0);
            stmt.setInt(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during status update: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?;";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during deletion: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        if (didCreateTable) return;

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                     "ID INTEGER PRIMARY KEY," +
                     "Password TEXT NOT NULL," +
                     "IsLoggedIn INTEGER NOT NULL DEFAULT 0" +
                     ");";
                     
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create Users table: " + e.getMessage());
        }
    }
}
