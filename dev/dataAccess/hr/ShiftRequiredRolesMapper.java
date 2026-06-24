package dataAccess.hr;

import java.sql.*;
import java.util.*;

public class ShiftRequiredRolesMapper {
    private final String connectionString;

    public ShiftRequiredRolesMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS shift_required_roles (
                shift_id TEXT,
                certification TEXT,
                required_count INTEGER,
                PRIMARY KEY (shift_id, certification)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create shift_required_roles table", e);
        }
    }

    public void saveRequiredRoles(String shiftId, Map<String, Integer> requiredRoles, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM shift_required_roles WHERE shift_id = ?;";
        String insertSql = "INSERT INTO shift_required_roles (shift_id, certification, required_count) VALUES (?, ?, ?);";

        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setString(1, shiftId);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
            for (Map.Entry<String, Integer> entry : requiredRoles.entrySet()) {
                ins.setString(1, shiftId);
                ins.setString(2, entry.getKey());
                ins.setInt(3, entry.getValue());
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    public Map<String, Integer> getRequiredRolesByShiftId(String shiftId, Connection conn) throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT certification, required_count FROM shift_required_roles WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("certification"), rs.getInt("required_count"));
                }
            }
        }
        return result;
    }

    public Map<String, Map<String, Integer>> getAllMappedByShiftId(Connection conn) throws SQLException {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        String sql = "SELECT shift_id, certification, required_count FROM shift_required_roles;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String shiftId = rs.getString("shift_id");
                String cert = rs.getString("certification");
                int count = rs.getInt("required_count");
                map.computeIfAbsent(shiftId, k -> new HashMap<>()).put(cert, count);
            }
        }
        return map;
    }

    public void deleteByShiftId(String shiftId, Connection conn) throws SQLException {
        String sql = "DELETE FROM shift_required_roles WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            pstmt.executeUpdate();
        }
    }
}