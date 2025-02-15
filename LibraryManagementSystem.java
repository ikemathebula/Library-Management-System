import java.io.*;
import java.util.*;

class Book {
    String title, author;
    int id;
    boolean isIssued;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }
}

public class LibraryManagementSystem {
    private static List<Book> books = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadBooks();
        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("3. View Books");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addBook(); break;
                case 2: viewBooks(); break;
                case 3: issueBook(); break;
                case 4: returnBook(); break;
                case 5: saveBooks(); System.exit(0);
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addBook() {
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
            System.out.println(book.id + ". " + book.title + " by " + book.author + " (" + (book.isIssued ? "Issued" : "Available") + ")");
        }
    }

    private static void issueBook() {
        System.out.print("Enter book ID to issue: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        for (Book book : books) {
            if (book.id == id && !book.isIssued) {
                book.isIssued = true;
                System.out.println("Book issued successfully!");
                return;
            }
        }
        System.out.println("Invalid ID or book already issued.");
    }

    private static void returnBook() {
        System.out.print("Enter book ID to return: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        for (Book book : books) {
            if (book.id == id && book.isIssued) {
                book.isIssued = false;
                System.out.println("Book returned successfully!");
                return;
            }
        }
        System.out.println("Invalid ID or book was not issued.");
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
}
