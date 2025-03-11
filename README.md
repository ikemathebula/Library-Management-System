# 📚 Library Management System
**A JavaFX-based Library Management System that allows users to register, log in, view available books, issue books, and return books. The system is connected to a MySQL database.**

---

## 🛠 Features
- **User Authentication**: Users can register and log in.
- **Book Management**: View available books, issue books, and return books.
- **Database Integration**: Stores books, users, and issued book records in MySQL.
- **Interactive UI**: Built using JavaFX.
- **Error Handling**: Alerts for errors and successful actions.

---

## 🚀 Technologies Used
- **Java** (JDK 23)
- **JavaFX** (for the UI)
- **MySQL** (for database storage)
- **JDBC** (to connect Java to MySQL)
- **IntelliJ IDEA** (for development)
- **Maven/Gradle** (optional for dependency management)

---

## 📝 Setup Instructions
### **1. Clone the Repository**
```sh
git clone https://github.com/ikemathebula/Library-Management-System.git
cd Library-Management-System
```

### **2. Set Up MySQL Database**
1. Open MySQL and create a database:
   ```sql
   CREATE DATABASE library_db;
   ```
2. Create the **users** table:
   ```sql
   CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL
   );
   ```
3. Create the **books** table:
   ```sql
   CREATE TABLE books (
       id INT AUTO_INCREMENT PRIMARY KEY,
       title VARCHAR(255) NOT NULL,
       author VARCHAR(255) NOT NULL,
       isIssued BOOLEAN DEFAULT FALSE,
       issuedTo INT DEFAULT NULL,
       FOREIGN KEY (issuedTo) REFERENCES users(id) ON DELETE SET NULL
   );
   ```
4. Insert sample books:
   ```sql
   INSERT INTO books (title, author) VALUES
   ('Harry Potter', 'J.K. Rowling'),
   ('The Theory of Everything', 'Stephen Hawking'),
   ('The Art of War', 'Sun Tzu'),
   ('The Great Gatsby', 'F. Scott Fitzgerald');
   ```

### **3. Configure Database Connection**
- Edit `DatabaseManager.java` to match your MySQL credentials:
  ```java
  private static final String URL = "jdbc:mysql://localhost:3306/library_db";
  private static final String USER = "root";
  private static final String PASSWORD = "your_password";
  ```

### **4. Run the Project**
- Open **IntelliJ IDEA**.
- Ensure **JavaFX and MySQL Connector** libraries are added.
- Run `LibraryUI.java` to start the UI.

---

## 📸 ![image](https://github.com/user-attachments/assets/03a49f2e-1cba-463a-b211-5501101764dc)

---

## 📌 Future Improvements
- [ ] Add a search bar for books.
- [ ] Improve UI design.
- [ ] Implement an admin panel for book management.
- [ ] Allow book reservations.

---

## 👨‍💻 Author
**Ike Mathebula**  
📧 Contact: **ikemathebula@gmail.com**  
👉 GitHub: **[ikemathebula](https://github.com/ikemathebula)**






