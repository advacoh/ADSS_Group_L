package dataAccess.hr;

import java.sql.*;
import java.util.*;

public class ShiftAssignmentsMapper {
    private final String connectionString;

    public ShiftAssignmentsMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS shift_assignments (
                shift_id TEXT,
                certification TEXT,
                employee_id INTEGER,
                PRIMARY KEY (shift_id, certification, employee_id)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create shift_assignments table", e);
        }
    }

    public void saveAssignments(String shiftId, Map<String, List<Integer>> assignments, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM shift_assignments WHERE shift_id = ?;";
        String insertSql = "INSERT INTO shift_assignments (shift_id, certification, employee_id) VALUES (?, ?, ?);";

        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setString(1, shiftId);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
            for (Map.Entry<String, List<Integer>> entry : assignments.entrySet()) {
                String cert = entry.getKey();
                for (Integer empId : entry.getValue()) {
                    ins.setString(1, shiftId);
                    ins.setString(2, cert);
                    ins.setInt(3, empId);
                    ins.addBatch();
                }
            }
            ins.executeBatch();
        }
    }

    public Map<String, List<Integer>> getAssignmentsByShiftId(String shiftId, Connection conn) throws SQLException {
        Map<String, List<Integer>> result = new HashMap<>();
        String sql = "SELECT certification, employee_id FROM shift_assignments WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String cert = rs.getString("certification");
                    int empId = rs.getInt("employee_id");
                    result.computeIfAbsent(cert, k -> new ArrayList<>()).add(empId);
                }
            }
        }
        return result;
    }

    public Map<String, Map<String, List<Integer>>> getAllMappedByShiftId(Connection conn) throws SQLException {
        Map<String, Map<String, List<Integer>>> map = new HashMap<>();
        String sql = "SELECT shift_id, certification, employee_id FROM shift_assignments;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String shiftId = rs.getString("shift_id");
                String cert = rs.getString("certification");
                int empId = rs.getInt("employee_id");
                map.computeIfAbsent(shiftId, k -> new HashMap<>())
                   .computeIfAbsent(cert, k -> new ArrayList<>())
                   .add(empId);
            }
        }
        return map;
    }

    public void deleteByShiftId(String shiftId, Connection conn) throws SQLException {
        String sql = "DELETE FROM shift_assignments WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            pstmt.executeUpdate();
        }
    }
}