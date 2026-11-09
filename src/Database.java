import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:pracownicy.db";

    public Database() throws SQLException {
        createTable();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS pracownicy (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    imie TEXT NOT NULL,
                    nazwisko TEXT NOT NULL,
                    stanowisko TEXT NOT NULL
                )
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public ResultSet getEmployees(Connection conn) throws SQLException {
        String sql = """
                SELECT *
                FROM pracownicy
                ORDER BY id
                """;

        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public Connection getConnection() throws SQLException {
        return connect();
    }

    public void addEmployee(String firstName, String lastName, String position) throws SQLException {
        String sql = """
                INSERT INTO pracownicy (imie, nazwisko, stanowisko)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, position);

            pstmt.executeUpdate();
        }
    }
}
