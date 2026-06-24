package dataAccess.hr;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

class SlotMapper {
    private final String connectionString;

    SlotMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS slot_submission (
                employee_id INTEGER,
                date DATE,
                shift_type TEXT,
                constraint_val INTEGER,
                preference INTEGER,
                PRIMARY KEY (employee_id, date, shift_type)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create slot_submission table", e);
        }
    }

    void saveSlots(int employeeId, List<SlotDTO> slots, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM slot_submission WHERE employee_id = ?;";
        String insertSql = "INSERT INTO slot_submission (employee_id, date, shift_type, constraint_val, preference) VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, employeeId);
            deleteStmt.executeUpdate();
        }
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (SlotDTO slot : slots) {
                insertStmt.setInt(1, employeeId);
                insertStmt.setString(2, slot.getDate().toString());
                insertStmt.setString(3, slot.getShiftType());
                insertStmt.setInt(4, slot.isConstraint() ? 1 : 0);
                insertStmt.setInt(5, slot.isPreference() ? 1 : 0);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    List<SlotDTO> getSlotsByEmployeeId(int employeeId, Connection conn) throws SQLException {
    List<SlotDTO> slots = new ArrayList<>();
    String sql = "SELECT date, shift_type, constraint_val, preference FROM slot_submission WHERE employee_id = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, employeeId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                slots.add(new SlotDTO(
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("shift_type"),
                    rs.getInt("constraint_val") == 1,
                    rs.getInt("preference") == 1
                ));
            }
        }
    }
    return slots;
}
    Map<Integer, List<SlotDTO>> getAllSlotsMappedByEmployeeId(Connection conn) throws SQLException {
    Map<Integer, List<SlotDTO>> map = new HashMap<>();
    String sql = "SELECT employee_id, date, shift_type, constraint_val, preference FROM slot_submission;";
    
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            int empId = rs.getInt("employee_id");
            SlotDTO slot = new SlotDTO(
                LocalDate.parse(rs.getString("date")),
                rs.getString("shift_type"),
                rs.getInt("constraint_val") == 1,
                rs.getInt("preference") == 1
            );
            map.computeIfAbsent(empId, k -> new ArrayList<>()).add(slot);
        }
    }
    return map;
}

    Set<Integer> getAvailableEmployeeIds(LocalDate date, String shiftType) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT employee_id FROM slot_submission WHERE date = ? AND shift_type = ? AND constraint_val = 1;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date.toString());
            pstmt.setString(2, shiftType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("employee_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch available employee ids", e);
        }
        return ids;
    }

    void deleteByEmployeeId(int employeeId, Connection conn) throws SQLException {
        String sql = "DELETE FROM slot_submission WHERE employee_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }

    
}