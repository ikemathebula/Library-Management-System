import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibraryUI extends Application {

    private Stage window;
    private Scene loginScene, libraryScene;
    private TextField userIdField, searchField;
    private Label welcomeLabel;
    private int loggedInUserId = -1; // Stores the logged-in user ID
    private TableView<Book> bookTable; // Table for displaying books

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Library Management System");

        // ------------------ Login Scene ------------------
        Label loginLabel = new Label("Enter User ID:");
        userIdField = new TextField();
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> loginUser());

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> showRegisterForm());

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.getChildren().addAll(loginLabel, userIdField, loginButton, registerButton);
        loginScene = new Scene(loginLayout, 300, 250);

        // ------------------ Library Scene ------------------
        welcomeLabel = new Label("Welcome!");
        searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.setOnKeyReleased(e -> searchBooks());

        // Buttons for issuing and returning books
        Button issueBookButton = new Button("Issue Book");
        issueBookButton.setOnAction(e -> issueBook());

        Button returnBookButton = new Button("Return Book");
        returnBookButton.setOnAction(e -> returnBook());

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> logoutUser());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        // TableView for displaying books
        bookTable = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        bookTable.getColumns().addAll(titleColumn, authorColumn, statusColumn);

        VBox libraryLayout = new VBox(10);
        libraryLayout.setPadding(new Insets(20));
        libraryLayout.getChildren().addAll(
                welcomeLabel,
                searchField,
                bookTable,
                issueBookButton,
                returnBookButton,
                logoutButton,
                exitButton
        );
        libraryScene = new Scene(libraryLayout, 600, 500);

        // Set login scene as the first screen
        window.setScene(loginScene);
        window.show();
    }

    // ------------------ LOGIN ------------------
    private void loginUser() {
        String userIdText = userIdField.getText();
        if (userIdText.isEmpty()) {
            showAlert("Error", "User ID cannot be empty.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, Integer.parseInt(userIdText));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id");
                welcomeLabel.setText("Welcome, " + rs.getString("name"));
                window.setScene(libraryScene);
                searchBooks();
            } else {
                showAlert("Login Failed", "User not found. Please register first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to database.");
        }
    }

    private void logoutUser() {
        loggedInUserId = -1;
        window.setScene(loginScene);
        showAlert("Logged Out", "You have successfully logged out.");
    }

    // ------------------ BOOK SEARCH ------------------
    private void searchBooks() {
        String searchQuery = searchField.getText();
        ObservableList<Book> books = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?"
             )) {
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getBoolean("isIssued")
                        ? "Issued to " + rs.getInt("issuedTo")
                        : "Available";
                books.add(new Book(rs.getString("title"), rs.getString("author"), status));
            }

            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to fetch books.");
        }
    }

    // ------------------ ISSUE & RETURN ------------------
    private void issueBook() {
        if (loggedInUserId == -1) {
            showAlert("Error", "Please log in first.");
            return;
        }

        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Select a book to issue.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE books SET isIssued = true, issuedTo = ? WHERE title = ? AND isIssued = false"
             )) {
            stmt.setInt(1, loggedInUserId);
            stmt.setString(2, selectedBook.getTitle());
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                showAlert("Success", "Book issued successfully!");
                searchBooks();
            } else {
                showAlert("Error", "Book is already issued.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to issue book.");
        }
    }

    private void returnBook() {
        if (loggedInUserId == -1) {
            showAlert("Error", "Please log in first.");
            return;
        }

        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Select a book to return.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE books SET isIssued = false, issuedTo = NULL WHERE title = ? AND issuedTo = ?"
             )) {
            stmt.setString(1, selectedBook.getTitle());
            stmt.setInt(2, loggedInUserId);
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                showAlert("Success", "Book returned successfully!");
                searchBooks();
            } else {
                showAlert("Error", "This book was not issued by you.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to return book.");
        }
    }

    // ------------------ REGISTRATION ------------------
    private void showRegisterForm() {
        Stage registerStage = new Stage();
        registerStage.setTitle("Register User");

        Label nameLabel = new Label("Enter Name:");
        TextField nameField = new TextField();
        Button registerBtn = new Button("Register");

        // Calls the registerUser method below
        registerBtn.setOnAction(e -> registerUser(nameField.getText(), registerStage));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(nameLabel, nameField, registerBtn);

        Scene scene = new Scene(layout, 300, 200);
        registerStage.setScene(scene);
        registerStage.show();
    }

    private void registerUser(String name, Stage registerStage) {
        // Make sure the name isn't empty
        if (name == null || name.trim().isEmpty()) {
            showAlert("Error", "Name cannot be empty.");
            return;
        }

        // Insert the user into your database
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (name) VALUES (?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {

            stmt.setString(1, name);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // Get the generated user ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    showAlert("Success", "User registered successfully! Your ID is: " + userId);
                }
                // Close the registration window
                registerStage.close();
            } else {
                showAlert("Error", "Failed to register user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to register user.");
        }
    }

    // ------------------ ALERT HELPER ------------------
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
