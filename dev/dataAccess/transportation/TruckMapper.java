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

import enums.LicenseType;

public class TruckMapper {
    private static final String TABLE_NAME = "Trucks";
    private final String connectionString;
    private boolean didCreateTable = false;

    public TruckMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    public TruckMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        if (didCreateTable) return;

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "LicenseNumber TEXT PRIMARY KEY," +
                "Model TEXT NOT NULL," +
                "NetWeight REAL NOT NULL," +
                "MaxCapacityWeight REAL NOT NULL," +
                "RequiredLicenseType TEXT NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create Trucks table: " + e.getMessage());
        }
    }

    public boolean insert(TruckDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (LicenseNumber, Model, NetWeight, MaxCapacityWeight, RequiredLicenseType) " +
                "VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dto.getLicenseNumber());
            stmt.setString(2, dto.getModel());
            stmt.setDouble(3, dto.getNetWeight());
            stmt.setDouble(4, dto.getMaxCapacityWeight());
            stmt.setString(5, dto.getRequiredLicenseType().name());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during truck insert: " + e.getMessage());
        }
    }

    public TruckDTO selectByLicenseNumber(String licenseNumber) {
        String sql = "SELECT LicenseNumber, Model, NetWeight, MaxCapacityWeight, RequiredLicenseType " +
                "FROM " + TABLE_NAME + " WHERE LicenseNumber = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licenseNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TruckDTO(
                            rs.getString("LicenseNumber"),
                            rs.getString("Model"),
                            rs.getDouble("NetWeight"),
                            rs.getDouble("MaxCapacityWeight"),
                            LicenseType.valueOf(rs.getString("RequiredLicenseType"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during truck lookup: " + e.getMessage());
        }

        return null;
    }

    public List<TruckDTO> selectAll() {
        List<TruckDTO> trucks = new ArrayList<>();

        String sql = "SELECT LicenseNumber, Model, NetWeight, MaxCapacityWeight, RequiredLicenseType " +
                "FROM " + TABLE_NAME + ";";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trucks.add(new TruckDTO(
                        rs.getString("LicenseNumber"),
                        rs.getString("Model"),
                        rs.getDouble("NetWeight"),
                        rs.getDouble("MaxCapacityWeight"),
                        LicenseType.valueOf(rs.getString("RequiredLicenseType"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during trucks select all: " + e.getMessage());
        }

        return trucks;
    }

    public boolean delete(String licenseNumber) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE LicenseNumber = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licenseNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during truck deletion: " + e.getMessage());
        }
    }
}