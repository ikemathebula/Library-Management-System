import java.io.*;
import java.util.*;

class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    String title, author;
    int id;
    boolean isIssued;
    int issuedTo; // Stores user ID who borrowed the book

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
        this.issuedTo = -1; // Default to no user
    }
}

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    int userId;
    List<Integer> borrowedBooks;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }
}

public class LibraryManagementSystem {
    private static List<Book> books = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static User loggedInUser = null;

    public static void main(String[] args) {
        loadBooks();
        loadUsers();

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
                case 8: saveBooks(); saveUsers(); System.exit(0);
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        User newUser = new User(users.size() + 1, name);
        users.add(newUser);
        System.out.println("User registered successfully! Your User ID is: " + newUser.userId);
    }

    private static void loginUser() {
        System.out.print("Enter your User ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        for (User user : users) {
            if (user.userId == userId) {
                loggedInUser = user;
                System.out.println("Login successful! Welcome, " + user.name);
                return;
            }
        }
        System.out.println("User not found. Please register first.");
    }

    private static void logoutUser() {
        if (loggedInUser != null) {
            System.out.println("Logged out successfully.");
            loggedInUser = null;
        } else {
            System.out.println("No user is logged in.");
        }
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
        books.add(new Book(books.size() + 1, title, author));
        System.out.println("Book added successfully!");
    }

    private static void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book book : books) {
            String status = book.isIssued ? "Issued to User ID " + book.issuedTo : "Available";
            System.out.println(book.id + ". " + book.title + " by " + book.author + " (" + status + ")");
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
        for (Book book : books) {
            if (book.id == id && !book.isIssued) {
                book.isIssued = true;
                book.issuedTo = loggedInUser.userId;
                loggedInUser.borrowedBooks.add(book.id);
                System.out.println("Book issued successfully to " + loggedInUser.name + "!");
                return;
            }
        }
        System.out.println("Invalid ID or book already issued.");
    }

    private static void returnBook() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.print("Enter book ID to return: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        for (Book book : books) {
            if (book.id == id && book.isIssued && book.issuedTo == loggedInUser.userId) {
                book.isIssued = false;
                book.issuedTo = -1;
                loggedInUser.borrowedBooks.remove(Integer.valueOf(id));
                System.out.println("Book returned successfully!");
                return;
            }
        }
        System.out.println("Invalid ID or book was not issued by you.");
    }

    private static void loadBooks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books.dat"))) {
            books = (List<Book>) ois.readObject();
        } catch (Exception e) {
            books = new ArrayList<>();
        }
    }

    private static void saveBooks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.dat"))) {
            oos.writeObject(books);
        } catch (IOException e) {
            System.out.println("Error saving books.");
        }
    }

    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            users = (List<User>) ois.readObject();
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }
}
