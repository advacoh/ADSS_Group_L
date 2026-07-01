package dataAccess.hr;


import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class EmployeeMapper {
    private final String connectionString;
    private final WeeklySubmissionMapper weeklySubmissionMapper;
    private final CertificationMapper certificationMapper;

    public EmployeeMapper(String connectionString) {
        this.connectionString = connectionString;
        this.weeklySubmissionMapper = new WeeklySubmissionMapper(connectionString);
        this.certificationMapper = new CertificationMapper(connectionString);
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS employee (
                id INTEGER PRIMARY KEY,
                name TEXT,
                bank_account INTEGER,
                start_date DATE,
                employment_type TEXT,
                salary_type TEXT,
                salary INTEGER,
                vacation INTEGER,
                will_overtime INTEGER,
                status TEXT,
                branch_id INTEGER
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee table", e);
        }
    }

    public boolean save(EmployeeDTO dto) {
    String checkSql = "SELECT id FROM employee WHERE id = ?;";
    String insertSql = """
        INSERT INTO employee (id, name, bank_account, start_date, employment_type,
            salary_type, salary, vacation, will_overtime, status, branch_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;
    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, dto.getId());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) { conn.rollback(); return false; }
                }
            }
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                fillStatement(ins, dto);
                ins.executeUpdate();
            }
            certificationMapper.saveCertifications(dto.getId(), dto.getCertifications(), conn);
            if (dto.getWeeklySubmission() != null)
                weeklySubmissionMapper.save(dto.getId(), dto.getWeeklySubmission(), conn); // ← was missing
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to save employee " + dto.getId(), e);
    }
    return true;
}

    public EmployeeDTO get(int employeeId) {
    String sql = "SELECT * FROM employee WHERE id = ?;";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, employeeId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Set<String> certs = certificationMapper.getCertificationsByEmployeeId(employeeId, conn);
                WeeklySubmissionDTO subDTO = weeklySubmissionMapper.getByEmployeeId(employeeId, conn);
                return toDTO(rs, certs, subDTO);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to fetch employee " + employeeId, e);
    }
    return null;
}

    public void update(EmployeeDTO dto) {
        String sql = """
            UPDATE employee SET name=?, bank_account=?, start_date=?, employment_type=?,
                salary_type=?, salary=?, vacation=?, will_overtime=?, status=?, branch_id=?
            WHERE id=?;
        """;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, dto.getName());
                    pstmt.setInt(2, dto.getBankAccount());
                    pstmt.setString(3, dto.getStartDate().toString());
                    pstmt.setString(4, dto.getEmploymentType());
                    pstmt.setString(5, dto.getSalaryType());
                    pstmt.setInt(6, dto.getSalary());
                    pstmt.setInt(7, dto.getVacation());
                    pstmt.setInt(8, dto.isWillOvertime() ? 1 : 0);
                    pstmt.setString(9, dto.getStatus());
                    pstmt.setInt(10, dto.getBranchId());
                    pstmt.setInt(11, dto.getId());
                    pstmt.executeUpdate();
                }
                certificationMapper.saveCertifications(dto.getId(), dto.getCertifications(), conn);
                if (dto.getWeeklySubmission() != null)
                weeklySubmissionMapper.save(dto.getId(), dto.getWeeklySubmission(), conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee " + dto.getId(), e);
        }
    }

    public void delete(int employeeId) {
        String sql = "DELETE FROM employee WHERE id = ?;";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                certificationMapper.deleteByEmployeeId(employeeId, conn);
                weeklySubmissionMapper.deleteByEmployeeId(employeeId, conn);
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, employeeId);
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee " + employeeId, e);
        }
    }

    public boolean doesHRExist() {
        Set<Integer> hrIds = certificationMapper.getEmployeeIdsByCertification("HR_MANAGER");
        if (hrIds.isEmpty()) return false;
        String inClause = String.join(",", Collections.nCopies(hrIds.size(), "?"));
        String sql = "SELECT id FROM employee WHERE status = 'ACTIVE' AND id IN (" + inClause + ") LIMIT 1;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;
            for (int id : hrIds) pstmt.setInt(i++, id);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check HR existence", e);
        }
    }

    public List<EmployeeDTO> getAvailableAndCertified(LocalDate date, String shiftType, String role) {
        return getAvailableAndCertifiedInternal(date, shiftType, role, null);
    }

    public List<EmployeeDTO> getAvailableAndCertified(int branchId, LocalDate date, String shiftType, String role) {
        return getAvailableAndCertifiedInternal(date, shiftType, role, branchId);
    }

    public List<EmployeeDTO> getByRole(String role) {
        return getByRoleInternal(role, null);
    }

    public List<EmployeeDTO> getByRole(int branchId, String role) {
        return getByRoleInternal(role, branchId);
    }

    // ── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private List<EmployeeDTO> getAvailableAndCertifiedInternal(LocalDate date, String shiftType, String role, Integer branchId) {
        Set<Integer> availableIds = weeklySubmissionMapper.getSlotMapper().getAvailableEmployeeIds(date, shiftType);
        Set<Integer> certifiedIds = certificationMapper.getEmployeeIdsByCertification(role);
        availableIds.retainAll(certifiedIds);
        if (availableIds.isEmpty()) return new ArrayList<>();
        return getEmployeesByIds(availableIds, branchId);
    }

    private List<EmployeeDTO> getByRoleInternal(String role, Integer branchId) {
        Set<Integer> certifiedIds = certificationMapper.getEmployeeIdsByCertification(role);
        if (certifiedIds.isEmpty()) return new ArrayList<>();
        return getEmployeesByIds(certifiedIds, branchId);
    }

    private List<EmployeeDTO> getEmployeesByIds(Set<Integer> ids, Integer branchId) {
        String inClause = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM employee WHERE status = 'ACTIVE' AND id IN (" + inClause + ")"
            + (branchId != null ? " AND (branch_id = ? OR branch_id = 0)" : "") + ";";

        try (Connection conn = getConnection()) {
            Map<Integer, Set<String>> allCerts = certificationMapper.getAllMappedByEmployeeId(conn);
            Map<Integer, WeeklySubmissionDTO> allSubs = weeklySubmissionMapper.getAllMappedByEmployeeId(conn);

            List<EmployeeDTO> result = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int i = 1;
                for (int id : ids) pstmt.setInt(i++, id);
                if (branchId != null) pstmt.setInt(i, branchId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int empId = rs.getInt("id");
                        result.add(toDTO(rs,
                            allCerts.getOrDefault(empId, new HashSet<>()),
                            allSubs.get(empId)));
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch employees by ids", e);
        }
    }

    private void fillStatement(PreparedStatement pstmt, EmployeeDTO dto) throws SQLException {
        pstmt.setInt(1, dto.getId());
        pstmt.setString(2, dto.getName());
        pstmt.setInt(3, dto.getBankAccount());
        pstmt.setString(4, dto.getStartDate().toString());
        pstmt.setString(5, dto.getEmploymentType());
        pstmt.setString(6, dto.getSalaryType());
        pstmt.setInt(7, dto.getSalary());
        pstmt.setInt(8, dto.getVacation());
        pstmt.setInt(9, dto.isWillOvertime() ? 1 : 0);
        pstmt.setString(10, dto.getStatus());
        pstmt.setInt(11, dto.getBranchId());
    }

    private EmployeeDTO toDTO(ResultSet rs, Set<String> certs, WeeklySubmissionDTO subDTO) throws SQLException {
        return new EmployeeDTO(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("bank_account"),
            LocalDate.parse(rs.getString("start_date")),
            rs.getString("employment_type"),
            rs.getString("salary_type"),
            rs.getInt("salary"),
            rs.getInt("vacation"),
            rs.getInt("will_overtime") == 1,
            rs.getString("status"),
            rs.getInt("branch_id"),
            certs,
            subDTO
        );
    }
}