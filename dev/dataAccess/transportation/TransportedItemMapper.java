package dataAccess.transportation;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransportedItemMapper {

    private static final String TABLE_NAME = "TransportedItems";
    private final String connectionString;
    private boolean didCreateTable = false;

    public TransportedItemMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    public TransportedItemMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        if (didCreateTable) return;

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "ItemId INTEGER NOT NULL," +
                "DocumentId INTEGER NOT NULL," +
                "ItemName TEXT NOT NULL," +
                "Quantity INTEGER NOT NULL," +
                "PRIMARY KEY (ItemId, DocumentId)" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create TransportedItems table: " + e.getMessage());
        }
    }

    public boolean insert(TransportedItemDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (ItemId, DocumentId, ItemName, Quantity) VALUES (?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getItemId());
            stmt.setInt(2, dto.getDocumentId());
            stmt.setString(3, dto.getItemName());
            stmt.setInt(4, dto.getQuantity());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transported item insert: " + e.getMessage());
        }
    }

    public List<TransportedItemDTO> selectByDocumentId(int documentId) {
        List<TransportedItemDTO> items = new ArrayList<>();

        String sql = "SELECT ItemId, DocumentId, ItemName, Quantity FROM " + TABLE_NAME +
                " WHERE DocumentId = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new TransportedItemDTO(
                            rs.getInt("ItemId"),
                            rs.getInt("DocumentId"),
                            rs.getString("ItemName"),
                            rs.getInt("Quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transported items lookup: " + e.getMessage());
        }

        return items;
    }

    public boolean deleteByDocumentId(int documentId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE DocumentId = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transported items deletion: " + e.getMessage());
        }
    }
}