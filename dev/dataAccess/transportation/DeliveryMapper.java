package dataAccess.transportation;

import enums.DeliveryStatus;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryMapper {

    private static final String TABLE_NAME = "Deliveries";
    private final String connectionString;
    private boolean didCreateTable = false;

    public DeliveryMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        if (didCreateTable) return;

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "ID INTEGER PRIMARY KEY," +
                "Date TEXT NOT NULL," +
                "DepartureTime TEXT NOT NULL," +
                "RecordedWeight REAL NOT NULL," +
                "Status TEXT NOT NULL," +
                "SourceSiteId INTEGER NOT NULL," +
                "TruckLicenseNumber TEXT NOT NULL," +
                "DriverId INTEGER NOT NULL," +
                "CurrentStep INTEGER NOT NULL," +
                "PendingReason TEXT" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create Deliveries table: " + e.getMessage());
        }
    }

    public boolean insert(DeliveryDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (ID, Date, DepartureTime, RecordedWeight, Status, SourceSiteId, TruckLicenseNumber, DriverId, CurrentStep, PendingReason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillStatement(stmt, dto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery insert: " + e.getMessage());
        }
    }

    public DeliveryDTO selectById(int id) {
        String sql = "SELECT ID, Date, DepartureTime, RecordedWeight, Status, SourceSiteId, TruckLicenseNumber, DriverId, CurrentStep, PendingReason " +
                "FROM " + TABLE_NAME + " WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildDTO(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery lookup: " + e.getMessage());
        }

        return null;
    }

    public List<DeliveryDTO> selectAll() {
        List<DeliveryDTO> deliveries = new ArrayList<>();

        String sql = "SELECT ID, Date, DepartureTime, RecordedWeight, Status, SourceSiteId, TruckLicenseNumber, DriverId, CurrentStep, PendingReason " +
                "FROM " + TABLE_NAME + ";";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                deliveries.add(buildDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error during deliveries select all: " + e.getMessage());
        }

        return deliveries;
    }

    public boolean update(DeliveryDTO dto) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "Date = ?, " +
                "DepartureTime = ?, " +
                "RecordedWeight = ?, " +
                "Status = ?, " +
                "SourceSiteId = ?, " +
                "TruckLicenseNumber = ?, " +
                "DriverId = ?, " +
                "CurrentStep = ?, " +
                "PendingReason = ? " +
                "WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getDate().toString());
            stmt.setString(2, dto.getDepartureTime().toString());
            stmt.setDouble(3, dto.getRecordedWeight());
            stmt.setString(4, dto.getStatus().name());
            stmt.setInt(5, dto.getSourceSiteId());
            stmt.setString(6, dto.getTruckLicenseNumber());
            stmt.setInt(7, dto.getDriverId());
            stmt.setInt(8, dto.getCurrentStep());
            stmt.setString(9, dto.getPendingReason());
            stmt.setInt(10, dto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery update: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Database error during delivery deletion: " + e.getMessage());
        }
    }

    private void fillStatement(PreparedStatement stmt, DeliveryDTO dto) throws SQLException {
        stmt.setInt(1, dto.getId());
        stmt.setString(2, dto.getDate().toString());
        stmt.setString(3, dto.getDepartureTime().toString());
        stmt.setDouble(4, dto.getRecordedWeight());
        stmt.setString(5, dto.getStatus().name());
        stmt.setInt(6, dto.getSourceSiteId());
        stmt.setString(7, dto.getTruckLicenseNumber());
        stmt.setInt(8, dto.getDriverId());
        stmt.setInt(9, dto.getCurrentStep());
        stmt.setString(10, dto.getPendingReason());
    }

    private DeliveryDTO buildDTO(ResultSet rs) throws SQLException {
        return new DeliveryDTO(
                rs.getInt("ID"),
                LocalDate.parse(rs.getString("Date")),
                LocalTime.parse(rs.getString("DepartureTime")),
                rs.getDouble("RecordedWeight"),
                DeliveryStatus.valueOf(rs.getString("Status")),
                rs.getInt("SourceSiteId"),
                rs.getString("TruckLicenseNumber"),
                rs.getInt("DriverId"),
                rs.getInt("CurrentStep"),
                rs.getString("PendingReason")
        );
    }
}