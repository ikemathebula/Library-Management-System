public class TestConnection {
    public static void main(String[] args) {
        try {
            DatabaseManager.getConnection();
            System.out.println("Connected to MySQL successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
