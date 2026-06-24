package dataAccess.hr;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverrideRequestMapper {
    private final String connectionString;

    public OverrideRequestMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS override_requests (
                id        TEXT PRIMARY KEY,
                hr_id     INTEGER NOT NULL,
                emp_id    INTEGER NOT NULL,
                date      TEXT NOT NULL,
                shift_type TEXT NOT NULL,
                role      TEXT NOT NULL,
                status    TEXT NOT NULL
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create override_requests table", e);
        }
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    public boolean save(OverrideRequestDTO dto) {
        String checkSql = "SELECT id FROM override_requests WHERE id = ?;";
        String insertSql = """
            INSERT INTO override_requests (id, hr_id, emp_id, date, shift_type, role, status)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """;
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
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save override request " + dto.getId(), e);
        }
        return true;
    }

    public OverrideRequestDTO get(String id) {
        String sql = "SELECT * FROM override_requests WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return toDTO(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch override request " + id, e);
        }
        return null;
    }

    public List<OverrideRequestDTO> getAll() {
        String sql = "SELECT * FROM override_requests;";
        List<OverrideRequestDTO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) result.add(toDTO(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all override requests", e);
        }
        return result;
    }

    public List<OverrideRequestDTO> getByEmployee(int empId) {
        String sql = "SELECT * FROM override_requests WHERE emp_id = ?;";
        List<OverrideRequestDTO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.add(toDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch override requests for employee " + empId, e);
        }
        return result;
    }

    public List<OverrideRequestDTO> getByHR(int hrId) {
        String sql = "SELECT * FROM override_requests WHERE hr_id = ?;";
        List<OverrideRequestDTO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hrId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.add(toDTO(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch override requests for HR " + hrId, e);
        }
        return result;
    }

    public void update(OverrideRequestDTO dto) {
        String sql = """
            UPDATE override_requests
            SET hr_id=?, emp_id=?, date=?, shift_type=?, role=?, status=?
            WHERE id=?;
        """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getHrId());
            pstmt.setInt(2, dto.getEmpId());
            pstmt.setString(3, dto.getDate().toString());
            pstmt.setString(4, dto.getShiftType());
            pstmt.setString(5, dto.getRole());
            pstmt.setString(6, dto.getStatus());
            pstmt.setString(7, dto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update override request " + dto.getId(), e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM override_requests WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete override request " + id, e);
        }
    }

    // ── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private void fillStatement(PreparedStatement pstmt, OverrideRequestDTO dto) throws SQLException {
        pstmt.setString(1, dto.getId());
        pstmt.setInt(2, dto.getHrId());
        pstmt.setInt(3, dto.getEmpId());
        pstmt.setString(4, dto.getDate().toString());
        pstmt.setString(5, dto.getShiftType());
        pstmt.setString(6, dto.getRole());
        pstmt.setString(7, dto.getStatus());
    }

    private OverrideRequestDTO toDTO(ResultSet rs) throws SQLException {
        return new OverrideRequestDTO(
            rs.getString("id"),
            rs.getInt("hr_id"),
            rs.getInt("emp_id"),
            LocalDate.parse(rs.getString("date")),
            rs.getString("shift_type"),
            rs.getString("role"),
            rs.getString("status")
        );
    }
}