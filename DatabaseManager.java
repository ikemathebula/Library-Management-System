import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root"; // Change if needed
    private static final String PASSWORD = "545453"; // Change your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            // Explicitly load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }

        // Return connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
