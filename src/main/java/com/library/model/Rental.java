package com.library.model;

import java.time.LocalDate;

public class Rental {
    private int id;
    private int bookId;
    private String borrowerName;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public Rental() {}

    public Rental(int id, int bookId, String borrowerName, LocalDate borrowedDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.borrowerName = borrowerName;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}