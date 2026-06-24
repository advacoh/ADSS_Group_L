package dataAccess.hr;

import java.sql.*;
import java.util.*;

public class ShiftOvertimeMapper {
    private final String connectionString;

    public ShiftOvertimeMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS shift_overtime (
                shift_id TEXT,
                employee_id INTEGER,
                PRIMARY KEY (shift_id, employee_id)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create shift_overtime table", e);
        }
    }

    public void saveOvertime(String shiftId, List<Integer> overtimeEmployees, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM shift_overtime WHERE shift_id = ?;";
        String insertSql = "INSERT INTO shift_overtime (shift_id, employee_id) VALUES (?, ?);";

        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setString(1, shiftId);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
            for (Integer empId : overtimeEmployees) {
                ins.setString(1, shiftId);
                ins.setInt(2, empId);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    public List<Integer> getOvertimeByShiftId(String shiftId, Connection conn) throws SQLException {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT employee_id FROM shift_overtime WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("employee_id"));
                }
            }
        }
        return result;
    }

    public Map<String, List<Integer>> getAllMappedByShiftId(Connection conn) throws SQLException {
        Map<String, List<Integer>> map = new HashMap<>();
        String sql = "SELECT shift_id, employee_id FROM shift_overtime;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String shiftId = rs.getString("shift_id");
                int empId = rs.getInt("employee_id");
                map.computeIfAbsent(shiftId, k -> new ArrayList<>()).add(empId);
            }
        }
        return map;
    }

    public void deleteByShiftId(String shiftId, Connection conn) throws SQLException {
        String sql = "DELETE FROM shift_overtime WHERE shift_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shiftId);
            pstmt.executeUpdate();
        }
    }
}