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

        // Login Scene
        Label loginLabel = new Label("Enter User ID:");
        userIdField = new TextField();
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> loginUser());

        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.getChildren().addAll(loginLabel, userIdField, loginButton);
        loginScene = new Scene(loginLayout, 300, 200);

        // Library Scene (Main)
        welcomeLabel = new Label("Welcome!");
        searchField = new TextField();
        searchField.setPromptText("Search books...");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks());

        // TableView for displaying books
        bookTable = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        bookTable.getColumns().addAll(titleColumn, authorColumn);

        VBox libraryLayout = new VBox(10);
        libraryLayout.setPadding(new Insets(20));
        libraryLayout.getChildren().addAll(welcomeLabel, searchField, searchButton, bookTable);
        libraryScene = new Scene(libraryLayout, 500, 400);

        // Set login scene as the first screen
        window.setScene(loginScene);
        window.show();
    }

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
            } else {
                showAlert("Login Failed", "User not found. Please register first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to database.");
        }
    }

    private void searchBooks() {
        String searchQuery = searchField.getText();
        ObservableList<Book> books = FXCollections.observableArrayList();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?")) {
            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(rs.getString("title"), rs.getString("author")));
            }

            bookTable.setItems(books);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to fetch books.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
