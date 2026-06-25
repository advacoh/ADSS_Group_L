package dataAccess.transportation;

import enums.LicenseType;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverMapper {

    private static final String TABLE_NAME = "Drivers";

    private final String connectionString;
    private boolean didCreateTable = false;

    public DriverMapper() {
        String path = new File("supermarket.db").getAbsolutePath();
        this.connectionString = "jdbc:sqlite:" + path;
        createTableIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionString);
    }

    private void createTableIfNotExists() {

        if (didCreateTable) {
            return;
        }

        String sql = """
            CREATE TABLE IF NOT EXISTS Drivers (
                ID INTEGER PRIMARY KEY,
                LicenseType TEXT NOT NULL
            );
        """;

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute(sql);
            didCreateTable = true;
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed creating Drivers table", e);
        }
    }

    public boolean insert(DriverDTO dto) {

        String sql = """
            INSERT INTO Drivers(ID, LicenseType)
            VALUES (?, ?);
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, dto.getId());
            stmt.setString(2, dto.getLicenseType().name());

            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public DriverDTO selectById(int id) {

        String sql = """
            SELECT ID, LicenseType
            FROM Drivers
            WHERE ID = ?;
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return new DriverDTO(
                            rs.getInt("ID"),
                            LicenseType.valueOf(
                                    rs.getString("LicenseType")
                            )
                    );
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<DriverDTO> selectAll() {

        List<DriverDTO> result = new ArrayList<>();

        String sql = """
            SELECT ID, LicenseType
            FROM Drivers;
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                result.add(
                        new DriverDTO(
                                rs.getInt("ID"),
                                LicenseType.valueOf(
                                        rs.getString("LicenseType")
                                )
                        )
                );
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public boolean delete(int id) {

        String sql = """
            DELETE FROM Drivers
            WHERE ID = ?;
        """;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}