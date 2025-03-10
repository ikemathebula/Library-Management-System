import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            // Attempt to get a connection to MySQL
            Connection conn = DatabaseManager.getConnection();
            System.out.println("Connected to MySQL successfully!");

            // Close the connection
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error: Unable to connect to MySQL.");
            e.printStackTrace();
        }
    }
}

