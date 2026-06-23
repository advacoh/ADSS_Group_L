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

import enums.SiteType;

public class SiteMapper {
    private static final String TABLE_NAME = "Sites";
    private final String connectionString;
    private boolean didCreateTable = false;

    public SiteMapper() {
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
                "Name TEXT NOT NULL," +
                "Address TEXT NOT NULL," +
                "PhoneNumber TEXT NOT NULL," +
                "ContactPerson TEXT NOT NULL," +
                "SiteType TEXT NOT NULL," +
                "ZoneId INTEGER NOT NULL," +
                "ZoneName TEXT NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            didCreateTable = true;
        } catch (SQLException e) {
            System.err.println("Failed to create Sites table: " + e.getMessage());
        }
    }

    public boolean insert(SiteDTO dto) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (ID, Name, Address, PhoneNumber, ContactPerson, SiteType, ZoneId, ZoneName) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dto.getId());
            stmt.setString(2, dto.getName());
            stmt.setString(3, dto.getAddress());
            stmt.setString(4, dto.getPhoneNumber());
            stmt.setString(5, dto.getContactPerson());
            stmt.setString(6, dto.getSiteType().name());
            stmt.setInt(7, dto.getZoneId());
            stmt.setString(8, dto.getZoneName());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during site insert: " + e.getMessage());
        }
    }

    public SiteDTO selectById(int id) {
        String sql = "SELECT ID, Name, Address, PhoneNumber, ContactPerson, SiteType, ZoneId, ZoneName " +
                "FROM " + TABLE_NAME + " WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SiteDTO(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("Address"),
                            rs.getString("PhoneNumber"),
                            rs.getString("ContactPerson"),
                            SiteType.valueOf(rs.getString("SiteType")),
                            rs.getInt("ZoneId"),
                            rs.getString("ZoneName")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during site lookup: " + e.getMessage());
        }

        return null;
    }

    public List<SiteDTO> selectAll() {
        List<SiteDTO> sites = new ArrayList<>();

        String sql = "SELECT ID, Name, Address, PhoneNumber, ContactPerson, SiteType, ZoneId, ZoneName " +
                "FROM " + TABLE_NAME + ";";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sites.add(new SiteDTO(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("Address"),
                        rs.getString("PhoneNumber"),
                        rs.getString("ContactPerson"),
                        SiteType.valueOf(rs.getString("SiteType")),
                        rs.getInt("ZoneId"),
                        rs.getString("ZoneName")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during sites select all: " + e.getMessage());
        }

        return sites;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ID = ?;";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during site deletion: " + e.getMessage());
        }
    }
}