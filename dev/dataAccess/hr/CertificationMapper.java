package dataAccess.hr;

import java.sql.*;
import java.util.*;

public class CertificationMapper {
    private final String connectionString;

    CertificationMapper(String connectionString) {
        this.connectionString = connectionString;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS employee_certifications (
                employee_id INTEGER,
                certification TEXT,
                PRIMARY KEY (employee_id, certification)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee_certifications table", e);
        }
    }

    
    void saveCertifications(int employeeId, Set<String> certifications, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM employee_certifications WHERE employee_id = ?;";
        String insertSql = "INSERT INTO employee_certifications (employee_id, certification) VALUES (?, ?);";

        try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
            del.setInt(1, employeeId);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
            for (String cert : certifications) {
                ins.setInt(1, employeeId);
                ins.setString(2, cert); // No more .name() needed, it's already a String!
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

   
    Set<String> getCertificationsByEmployeeId(int employeeId, Connection conn) throws SQLException {
    Set<String> certs = new HashSet<>();
    String sql = "SELECT certification FROM employee_certifications WHERE employee_id = ?;";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, employeeId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) certs.add(rs.getString("certification"));
        }
    }
    return certs;
}

   
    Set<Integer> getEmployeeIdsByCertification(String cert) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT employee_id FROM employee_certifications WHERE certification = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cert);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("employee_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch employee ids for cert " + cert, e);
        }
        return ids;
    }

    
    Map<Integer, Set<String>> getAllMappedByEmployeeId(Connection conn) throws SQLException {
        Map<Integer, Set<String>> map = new HashMap<>();
        String sql = "SELECT employee_id, certification FROM employee_certifications;";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int empId = rs.getInt("employee_id");
                String cert = rs.getString("certification");
                map.computeIfAbsent(empId, k -> new HashSet<>()).add(cert);
            }
        }
        return map;
    }

    void deleteByEmployeeId(int employeeId, Connection conn) throws SQLException {
        String sql = "DELETE FROM employee_certifications WHERE employee_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();
        }
    }
}