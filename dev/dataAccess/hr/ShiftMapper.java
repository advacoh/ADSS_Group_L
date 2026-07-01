package dataAccess.hr;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ShiftMapper {
    private final String connectionString;
    private final ShiftRequiredRolesMapper requiredRolesMapper;
    private final ShiftAssignmentsMapper assignmentsMapper;
    private final ShiftOvertimeMapper overtimeMapper;

    public ShiftMapper(String connectionString) {
        this.connectionString = connectionString;
        this.requiredRolesMapper = new ShiftRequiredRolesMapper(connectionString);
        this.assignmentsMapper = new ShiftAssignmentsMapper(connectionString);
        this.overtimeMapper = new ShiftOvertimeMapper(connectionString);
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS shifts (
                id TEXT PRIMARY KEY,
                branch_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                shift_type TEXT NOT NULL,
                is_active INTEGER NOT NULL
            );
        """;
        String indexSql = "CREATE INDEX IF NOT EXISTS idx_shifts_is_active ON shifts(is_active);";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(indexSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create shifts table", e);
        }
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    public boolean save(ShiftDTO dto) {
        String checkSql = "SELECT id FROM shifts WHERE id = ?;";
        String insertSql = "INSERT INTO shifts (id, branch_id, date, shift_type, is_active) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setString(1, dto.getId());
                    try (ResultSet rs = check.executeQuery()) {
                        if (rs.next()) { conn.rollback(); return false; }
                    }
                }
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    fillStatement(ins, dto);
                    ins.executeUpdate();
                }
                requiredRolesMapper.saveRequiredRoles(dto.getId(), dto.getRequiredRoles(), conn);
                assignmentsMapper.saveAssignments(dto.getId(), dto.getAssignments(), conn);
                overtimeMapper.saveOvertime(dto.getId(), dto.getOvertimeEmployees(), conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save shift " + dto.getId(), e);
        }
        return true;
    }

    public void update(ShiftDTO dto) {
        String sql = "UPDATE shifts SET branch_id=?, date=?, shift_type=?, is_active=? WHERE id=?;";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, dto.getBranchId());
                    pstmt.setString(2, dto.getDate().toString());
                    pstmt.setString(3, dto.getShiftType());
                    pstmt.setInt(4, dto.isActive() ? 1 : 0);
                    pstmt.setString(5, dto.getId());
                    pstmt.executeUpdate();
                }
                requiredRolesMapper.saveRequiredRoles(dto.getId(), dto.getRequiredRoles(), conn);
                assignmentsMapper.saveAssignments(dto.getId(), dto.getAssignments(), conn);
                overtimeMapper.saveOvertime(dto.getId(), dto.getOvertimeEmployees(), conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update shift " + dto.getId(), e);
        }
    }

    public void delete(String shiftId) {
        String sql = "DELETE FROM shifts WHERE id = ?;";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                requiredRolesMapper.deleteByShiftId(shiftId, conn);
                assignmentsMapper.deleteByShiftId(shiftId, conn);
                overtimeMapper.deleteByShiftId(shiftId, conn);
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, shiftId);
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete shift " + shiftId, e);
        }
    }

    public void archive(int branchId, LocalDate date, String shiftType) {
        String sql = "UPDATE shifts SET is_active = 0 WHERE branch_id = ? AND date = ? AND shift_type = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            pstmt.setString(2, date.toString());
            pstmt.setString(3, shiftType);
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("Cannot archive: shift not found for branch " + branchId
                        + " on " + date + " " + shiftType);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to archive shift for branch " + branchId, e);
        }
    }

    // ── SINGLE-SHIFT READS ──────────────────────────────────────────────────

    public ShiftDTO get(int branchId, LocalDate date, String shiftType) {
        String sql = "SELECT * FROM shifts WHERE branch_id = ? AND date = ? AND shift_type = ? AND is_active = 1;";
        return getOne(sql, branchId, date, shiftType);
    }

    public ShiftDTO getPast(int branchId, LocalDate date, String shiftType) {
        String sql = "SELECT * FROM shifts WHERE branch_id = ? AND date = ? AND shift_type = ? AND is_active = 0;";
        return getOne(sql, branchId, date, shiftType);
    }

    private ShiftDTO getOne(String sql, int branchId, LocalDate date, String shiftType) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, branchId);
            pstmt.setString(2, date.toString());
            pstmt.setString(3, shiftType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String shiftId = rs.getString("id");
                    Map<String, Integer> roles = requiredRolesMapper.getRequiredRolesByShiftId(shiftId, conn);
                    Map<String, List<Integer>> assigns = assignmentsMapper.getAssignmentsByShiftId(shiftId, conn);
                    List<Integer> overtime = overtimeMapper.getOvertimeByShiftId(shiftId, conn);
                    return toDTO(rs, roles, assigns, overtime);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch shift for branch " + branchId, e);
        }
        return null;
    }

    // ── MULTI-SHIFT READS (batched sub-mapper lookups) ─────────────────────

    public List<ShiftDTO> getByBranchAndDate(int branchId, LocalDate date) {
        String sql = "SELECT * FROM shifts WHERE branch_id = ? AND date = ? AND is_active = 1;";
        return getMany(sql, ps -> {
            ps.setInt(1, branchId);
            ps.setString(2, date.toString());
        });
    }

    public List<ShiftDTO> getAllActiveShifts(int branchId) {
        String sql = "SELECT * FROM shifts WHERE branch_id = ? AND is_active = 1;";
        return getMany(sql, ps -> ps.setInt(1, branchId));
    }

    public List<ShiftDTO> getAllActiveShifts() {
        String sql = "SELECT * FROM shifts WHERE is_active = 1;";
        return getMany(sql, ps -> { });
    }

    public List<ShiftDTO> getShiftsByDateAndType(LocalDate date, String shiftType) {
        String sql = "SELECT * FROM shifts WHERE date = ? AND shift_type = ? AND is_active = 1;";
        return getMany(sql, ps -> {
            ps.setString(1, date.toString());
            ps.setString(2, shiftType);
        });
    }

    private interface StatementFiller {
        void fill(PreparedStatement ps) throws SQLException;
    }

    private List<ShiftDTO> getMany(String sql, StatementFiller filler) {
        List<ShiftDTO> result = new ArrayList<>();
        try (Connection conn = getConnection()) {
            Map<String, Map<String, Integer>> allRoles = requiredRolesMapper.getAllMappedByShiftId(conn);
            Map<String, Map<String, List<Integer>>> allAssigns = assignmentsMapper.getAllMappedByShiftId(conn);
            Map<String, List<Integer>> allOvertime = overtimeMapper.getAllMappedByShiftId(conn);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                filler.fill(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String shiftId = rs.getString("id");
                        result.add(toDTO(rs,
                                allRoles.getOrDefault(shiftId, new HashMap<>()),
                                allAssigns.getOrDefault(shiftId, new HashMap<>()),
                                allOvertime.getOrDefault(shiftId, new ArrayList<>())));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch shifts", e);
        }
        return result;
    }

    // ── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private void fillStatement(PreparedStatement pstmt, ShiftDTO dto) throws SQLException {
        pstmt.setString(1, dto.getId());
        pstmt.setInt(2, dto.getBranchId());
        pstmt.setString(3, dto.getDate().toString());
        pstmt.setString(4, dto.getShiftType());
        pstmt.setInt(5, dto.isActive() ? 1 : 0);
    }

    private ShiftDTO toDTO(ResultSet rs, Map<String, Integer> roles,
                            Map<String, List<Integer>> assigns, List<Integer> overtime) throws SQLException {
        return new ShiftDTO(
                rs.getString("id"),
                rs.getInt("branch_id"),
                LocalDate.parse(rs.getString("date")),
                rs.getString("shift_type"),
                rs.getInt("is_active") == 1,
                roles,
                assigns,
                overtime
        );
    }
}