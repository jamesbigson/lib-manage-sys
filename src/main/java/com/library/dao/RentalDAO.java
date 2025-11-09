package com.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Rental;
import com.library.util.DatabaseConnection;

public class RentalDAO {
    
    public void rentBook(Rental rental) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        try {
            // First check if book is available
            String checkSql = "SELECT available_copies FROM books WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, rental.getBookId());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt("available_copies") > 0) {
                    // Update book available copies
                    String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
                    try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {
                        updateBookStmt.setInt(1, rental.getBookId());
                        updateBookStmt.executeUpdate();
                    }
                    
                    // Insert rental record
                    String insertRentalSql = "INSERT INTO rentals (book_id, borrower_name, borrowed_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertRentalStmt = conn.prepareStatement(insertRentalSql)) {
                        insertRentalStmt.setInt(1, rental.getBookId());
                        insertRentalStmt.setString(2, rental.getBorrowerName());
                        insertRentalStmt.setDate(3, Date.valueOf(rental.getBorrowedDate()));
                        insertRentalStmt.setDate(4, Date.valueOf(rental.getDueDate()));
                        insertRentalStmt.setString(5, "BORROWED");
                        insertRentalStmt.executeUpdate();
                    }
                    
                    conn.commit();
                } else {
                    throw new SQLException("Book not available for rent");
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    
    public void returnBook(int rentalId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        
        try {
            // Get book ID from rental
            String getRentalSql = "SELECT book_id FROM rentals WHERE id = ? AND status = 'BORROWED'";
            try (PreparedStatement getRentalStmt = conn.prepareStatement(getRentalSql)) {
                getRentalStmt.setInt(1, rentalId);
                ResultSet rs = getRentalStmt.executeQuery();
                
                if (rs.next()) {
                    int bookId = rs.getInt("book_id");
                    
                    // Update rental status
                    String updateRentalSql = "UPDATE rentals SET status = 'RETURNED', return_date = ? WHERE id = ?";
                    try (PreparedStatement updateRentalStmt = conn.prepareStatement(updateRentalSql)) {
                        updateRentalStmt.setDate(1, Date.valueOf(LocalDate.now()));
                        updateRentalStmt.setInt(2, rentalId);
                        updateRentalStmt.executeUpdate();
                    }
                    
                    // Update book available copies
                    String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
                    try (PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql)) {
                        updateBookStmt.setInt(1, bookId);
                        updateBookStmt.executeUpdate();
                    }
                    
                    conn.commit();
                } else {
                    throw new SQLException("Rental not found or book already returned");
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    
    public List<Rental> getRentalsByBookId(int bookId) throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setBookId(rs.getInt("book_id"));
                rental.setBorrowerName(rs.getString("borrower_name"));
                rental.setBorrowedDate(rs.getDate("borrowed_date").toLocalDate());
                rental.setDueDate(rs.getDate("due_date").toLocalDate());
                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    rental.setReturnDate(returnDate.toLocalDate());
                }
                rental.setStatus(rs.getString("status"));
                rentals.add(rental);
            }
        }
        return rentals;
    }
    
    public List<Rental> getAllActiveRentals() throws SQLException {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.*, b.title as book_title FROM rentals r " +
                    "JOIN books b ON r.book_id = b.id " +
                    "WHERE r.status = 'BORROWED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setBookId(rs.getInt("book_id"));
                rental.setBorrowerName(rs.getString("borrower_name"));
                rental.setBorrowedDate(rs.getDate("borrowed_date").toLocalDate());
                rental.setDueDate(rs.getDate("due_date").toLocalDate());
                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    rental.setReturnDate(returnDate.toLocalDate());
                }
                rental.setStatus(rs.getString("status"));
                rentals.add(rental);
            }
        }
        return rentals;
    }
}