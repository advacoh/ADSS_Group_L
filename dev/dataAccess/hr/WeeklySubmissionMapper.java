package dataAccess.hr;

import java.sql.*;
import java.util.*;

class WeeklySubmissionMapper {
    private final String connectionString;
    private final SlotMapper slotMapper;

    WeeklySubmissionMapper(String connectionString) {
        this.connectionString = connectionString;
        this.slotMapper = new SlotMapper(connectionString);
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS weekly_submission (
                employee_id INTEGER PRIMARY KEY,
                day_off INTEGER,
                double_shift_allowed INTEGER
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create weekly_submission table", e);
        }
    }

   void save(int employeeId, WeeklySubmissionDTO dto, Connection conn) throws SQLException {
    String deleteSql = "DELETE FROM weekly_submission WHERE employee_id = ?;";
    String insertSql = "INSERT INTO weekly_submission (employee_id, day_off, double_shift_allowed) VALUES (?, ?, ?);";

    try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
        del.setInt(1, employeeId);
        del.executeUpdate();
    }
    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
        ins.setInt(1, employeeId);
        ins.setInt(2, dto.getDayOff());
        ins.setInt(3, dto.isDoubleShiftAllowed() ? 1 : 0);
        ins.executeUpdate();
    }
    slotMapper.saveSlots(employeeId, dto.getSlots(), conn);
}

   WeeklySubmissionDTO getByEmployeeId(int employeeId, Connection conn) throws SQLException {
    String sql = "SELECT day_off, double_shift_allowed FROM weekly_submission WHERE employee_id = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, employeeId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                List<SlotDTO> slots = slotMapper.getSlotsByEmployeeId(employeeId, conn);
                return new WeeklySubmissionDTO(
                    rs.getInt("day_off"),
                    rs.getInt("double_shift_allowed") == 1,
                    slots
                );
            }
        }
    }
    return null;
}

    // Bulk load — one query each for submissions and slots, assembled in Java
    Map<Integer, WeeklySubmissionDTO> getAllMappedByEmployeeId(Connection conn) throws SQLException {
    Map<Integer, WeeklySubmissionDTO> result = new HashMap<>();
    String sql = "SELECT employee_id, day_off, double_shift_allowed FROM weekly_submission;";

    // Pass the connection further down to the slot mapper helper
    Map<Integer, List<SlotDTO>> allSlots = slotMapper.getAllSlotsMappedByEmployeeId(conn);

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            int empId = rs.getInt("employee_id");
            result.put(empId, new WeeklySubmissionDTO(
                rs.getInt("day_off"),
                rs.getInt("double_shift_allowed") == 1,
                allSlots.getOrDefault(empId, new ArrayList<>())
            ));
        }
    }
    return result;
}

    void deleteByEmployeeId(int employeeId, Connection conn) throws SQLException {
        slotMapper.deleteByEmployeeId(employeeId, conn);
        String sql = "DELETE FROM weekly_submission WHERE employee_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }

    SlotMapper getSlotMapper() { return slotMapper; }
}