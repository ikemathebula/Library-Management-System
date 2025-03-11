import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestConnection {
    private static final Logger LOGGER = Logger.getLogger(TestConnection.class.getName());

    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            LOGGER.info("Connected to MySQL successfully!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error: Unable to connect to MySQL", e);
        }
    }
}

