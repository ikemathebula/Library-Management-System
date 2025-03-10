import java.sql.*;
import java.util.*;


class User {
    int userId;
    String name;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}

public class LibraryManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static User loggedInUser = null;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Register User");
            System.out.println("2. Login");
            System.out.println("3. Add Book");
            System.out.println("4. View Books");
            System.out.println("5. Issue Book");
            System.out.println("6. Return Book");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: registerUser(); break;
                case 2: loginUser(); break;
                case 3: addBook(); break;
                case 4: viewBooks(); break;
                case 5: issueBook(); break;
                case 6: returnBook(); break;
                case 7: logoutUser(); break;
                case 8: System.exit(0);
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                System.out.println("User registered successfully! Your User ID is: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loginUser() {
        System.out.print("Enter your User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUser = new User(rs.getInt("id"), rs.getString("name"));
                System.out.println("Login successful! Welcome, " + loggedInUser.name);
            } else {
                System.out.println("User not found. Please register first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void logoutUser() {
        loggedInUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void addBook() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, author, isIssued, issuedTo) VALUES (?, ?, false, NULL)")) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewBooks() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                String status = rs.getBoolean("isIssued") ? "Issued to User ID " + rs.getInt("issuedTo") : "Available";
                System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " by " + rs.getString("author") + " (" + status + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void issueBook() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter book ID to issue: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET isIssued = true, issuedTo = ? WHERE id = ? AND isIssued = false")) {
            stmt.setInt(1, loggedInUser.userId);
            stmt.setInt(2, id);
            int updated = stmt.executeUpdate();
            if (updated > 0) System.out.println("Book issued successfully!");
            else System.out.println("Invalid ID or book already issued.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void returnBook() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter book ID to return: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE books SET isIssued = false, issuedTo = NULL WHERE id = ? AND issuedTo = ?")) {
            stmt.setInt(1, id);
            stmt.setInt(2, loggedInUser.userId);
            int updated = stmt.executeUpdate();
            if (updated > 0) System.out.println("Book returned successfully!");
            else System.out.println("Invalid ID or book was not issued by you.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
