/*
Library Management System with MySQL
Features: Add, View, Issue, and Return books
Uses Java and MySQL via JDBC
*/

import java.sql.*;
import java.util.Scanner;

public class LibraryManagementJDBC {
    static final String DB_URL = "jdbc:mysql://localhost:3306/librarydb";
    static final String USER = "root"; 
    static final String PASS = "";     

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            Statement stmt = conn.createStatement();
            String createTable = "CREATE TABLE IF NOT EXISTS books (" +
                                 "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                 "title VARCHAR(100), " +
                                 "author VARCHAR(100), " +
                                 "isIssued BOOLEAN DEFAULT FALSE)";
            stmt.executeUpdate(createTable);

            int choice;
            do {
                System.out.println("\n--- Library Management System ---");
                System.out.println("1. Add Book");
                System.out.println("2. View Books");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch(choice) {
                    case 1: addBook(conn); break;
                    case 2: viewBooks(conn); break;
                    case 3: issueBook(conn); break;
                    case 4: returnBook(conn); break;
                    case 5: System.out.println("Exiting..."); break;
                    default: System.out.println("Invalid choice!");
                }
            } while(choice != 5);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed. Make sure MySQL is running and database exists.");
        }
    }

    static void addBook(Connection conn) throws SQLException {
        System.out.print("Enter book title: ");
        String title = sc.nextLine();
        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        String insertQuery = "INSERT INTO books (title, author) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
        pstmt.setString(1, title);
        pstmt.setString(2, author);
        pstmt.executeUpdate();
        System.out.println("Book added successfully!");
    }

    static void viewBooks(Connection conn) throws SQLException {
        String selectQuery = "SELECT * FROM books";
        ResultSet rs = conn.createStatement().executeQuery(selectQuery);

        System.out.println("\nList of Books:");
        while(rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String author = rs.getString("author");
            boolean isIssued = rs.getBoolean("isIssued");
            System.out.println(id + ". " + title + " by " + author + " | " + (isIssued ? "Issued" : "Available"));
        }
    }

    static void issueBook(Connection conn) throws SQLException {
        viewBooks(conn);
        System.out.print("Enter book ID to issue: ");
        int id = sc.nextInt();

        String checkQuery = "SELECT isIssued FROM books WHERE id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if(rs.next()) {
            if(!rs.getBoolean("isIssued")) {
                String updateQuery = "UPDATE books SET isIssued = TRUE WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, id);
                updateStmt.executeUpdate();
                System.out.println("Book issued successfully!");
            } else {
                System.out.println("Book is already issued.");
            }
        } else {
            System.out.println("Book ID not found.");
        }
    }

    static void returnBook(Connection conn) throws SQLException {
        viewBooks(conn);
        System.out.print("Enter book ID to return: ");
        int id = sc.nextInt();

        String checkQuery = "SELECT isIssued FROM books WHERE id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if(rs.next()) {
            if(rs.getBoolean("isIssued")) {
                String updateQuery = "UPDATE books SET isIssued = FALSE WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, id);
                updateStmt.executeUpdate();
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Book was not issued.");
            }
        } else {
            System.out.println("Book ID not found.");
        }
    }
}
