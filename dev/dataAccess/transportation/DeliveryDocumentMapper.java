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

public class DeliveryDocumentMapper {

    private static final String TABLE_NAME = "DeliveryDocuments";
    private final String connectionString;
    private boolean didCreateTable = false;

    public DeliveryDocumentMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    public DeliveryDocumentMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        if (didCreateTable) return;

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "DocumentId INTEGER PRIMARY KEY," +
                "DeliveryId INTEGER NOT NULL," +
                "DestinationSiteId INTEGER NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create DeliveryDocuments table: " + e.getMessage());
        }
    }

    public boolean insert(DeliveryDocumentDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (DocumentId, DeliveryId, DestinationSiteId) VALUES (?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getDocumentId());
            stmt.setInt(2, dto.getDeliveryId());
            stmt.setInt(3, dto.getDestinationSiteId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery document insert: " + e.getMessage());
        }
    }

    public List<DeliveryDocumentDTO> selectByDeliveryId(int deliveryId) {
        List<DeliveryDocumentDTO> documents = new ArrayList<>();

        String sql = "SELECT DocumentId, DeliveryId, DestinationSiteId FROM " + TABLE_NAME +
                " WHERE DeliveryId = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deliveryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documents.add(new DeliveryDocumentDTO(
                            rs.getInt("DocumentId"),
                            rs.getInt("DeliveryId"),
                            rs.getInt("DestinationSiteId")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery documents lookup: " + e.getMessage());
        }

        return documents;
    }

    public boolean deleteByDeliveryId(int deliveryId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE DeliveryId = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deliveryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery documents deletion: " + e.getMessage());
        }
    }
}