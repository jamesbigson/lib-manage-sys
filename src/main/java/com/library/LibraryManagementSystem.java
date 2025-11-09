package com.library;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.library.dao.BookDAO;
import com.library.dao.RentalDAO;
import com.library.model.Book;
import com.library.model.Rental;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryManagementSystem extends Application {
    private final BookDAO bookDAO = new BookDAO();
    private final RentalDAO rentalDAO = new RentalDAO();
    private final TableView<Book> tableView = new TableView<>();
    private final TableView<Rental> rentalTableView = new TableView<>();
    private final TextField titleField = new TextField();
    private final TextField authorField = new TextField();
    private final TextField isbnField = new TextField();
    private final TextField quantityField = new TextField();
    private final TextField borrowerNameField = new TextField();
    private final DatePicker dueDatePicker = new DatePicker();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");

        // Create tab pane
        TabPane tabPane = new TabPane();
        
        // Books tab
        Tab booksTab = new Tab("Books Management");
        booksTab.setClosable(false);
        VBox booksLayout = new VBox(10);
        booksLayout.setPadding(new Insets(10));
        booksTab.setContent(booksLayout);

        // Rentals tab
        Tab rentalsTab = new Tab("Book Rentals");
        rentalsTab.setClosable(false);
        VBox rentalsLayout = new VBox(10);
        rentalsLayout.setPadding(new Insets(10));
        rentalsTab.setContent(rentalsLayout);

        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Form for adding/updating books
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        form.add(new Label("Title:"), 0, 0);
        form.add(titleField, 1, 0);
        form.add(new Label("Author:"), 0, 1);
        form.add(authorField, 1, 1);
        form.add(new Label("ISBN:"), 0, 2);
        form.add(isbnField, 1, 2);
        form.add(new Label("Quantity:"), 0, 3);
        form.add(quantityField, 1, 3);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Book");
        Button updateButton = new Button("Update Book");
        Button deleteButton = new Button("Delete Book");
        Button clearButton = new Button("Clear Fields");
        Button refreshBooksButton = new Button("Refresh");
        refreshBooksButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton, refreshBooksButton);

        // Table setup
        TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIsbn()));

        TableColumn<Book, Integer> quantityColumn = new TableColumn<>("Total Copies");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<Book, Integer> availableColumn = new TableColumn<>("Available Copies");
        availableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAvailableCopies()).asObject());

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Available" : "Not Available"));

        tableView.getColumns().addAll(idColumn, titleColumn, authorColumn, isbnColumn, quantityColumn, availableColumn, statusColumn);

        // Add Book Management components to books tab
        booksLayout.getChildren().addAll(form, buttonBox, tableView);

        // Create Rental Management UI
        GridPane rentalForm = new GridPane();
        rentalForm.setHgap(10);
        rentalForm.setVgap(10);
        rentalForm.setPadding(new Insets(10));

        rentalForm.add(new Label("Borrower Name:"), 0, 0);
        rentalForm.add(borrowerNameField, 1, 0);
        rentalForm.add(new Label("Due Date:"), 0, 1);
        rentalForm.add(dueDatePicker, 1, 1);

        // Rental Buttons
        HBox rentalButtonBox = new HBox(10);
        Button rentButton = new Button("Rent Book");
        Button returnButton = new Button("Return Book");
        Button refreshRentalsButton = new Button("Refresh");
        refreshRentalsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        rentalButtonBox.getChildren().addAll(rentButton, returnButton, refreshRentalsButton);

        // Setup rental table
        TableColumn<Rental, Integer> rentalIdColumn = new TableColumn<>("Rental ID");
        rentalIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Rental, String> borrowerColumn = new TableColumn<>("Borrower");
        borrowerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowerName()));

        TableColumn<Rental, String> bookTitleColumn = new TableColumn<>("Book Title");
        bookTitleColumn.setCellValueFactory(cellData -> {
            try {
                Book book = bookDAO.getBookById(cellData.getValue().getBookId());
                return new SimpleStringProperty(book != null ? book.getTitle() : "");
            } catch (SQLException e) {
                return new SimpleStringProperty("Error");
            }
        });

        TableColumn<Rental, String> rentalStatusColumn = new TableColumn<>("Status");
        rentalStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        rentalTableView.getColumns().addAll(rentalIdColumn, borrowerColumn, bookTitleColumn, rentalStatusColumn);

        // Create search box for books
        HBox bookSearchBox = new HBox(10);
        TextField bookSearchField = new TextField();
        bookSearchField.setPromptText("Search books by title, author, or ISBN...");
        bookSearchField.setPrefWidth(300);
        Button bookSearchButton = new Button("Search Books");
        bookSearchBox.getChildren().addAll(new Label("Search Books:"), bookSearchField, bookSearchButton);

        // Create a second table view for books in rental tab
        TableView<Book> rentalBooksTableView = new TableView<>();
        rentalBooksTableView.getColumns().addAll(idColumn, titleColumn, authorColumn, quantityColumn, availableColumn, statusColumn);
        
        // Add label above the books table
        Label selectBookLabel = new Label("Available Books:");
        selectBookLabel.setStyle("-fx-font-weight: bold");

        // Add search functionality for books
        bookSearchButton.setOnAction(e -> {
            String searchText = bookSearchField.getText().toLowerCase();
            try {
                List<Book> allBooks = bookDAO.getAllBooks();
                rentalBooksTableView.getItems().clear();
                if (searchText.isEmpty()) {
                    rentalBooksTableView.getItems().addAll(allBooks);
                } else {
                    rentalBooksTableView.getItems().addAll(
                        allBooks.stream()
                            .filter(book -> 
                                book.getTitle().toLowerCase().contains(searchText) ||
                                book.getAuthor().toLowerCase().contains(searchText) ||
                                book.getIsbn().toLowerCase().contains(searchText))
                            .collect(Collectors.toList())
                    );
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error searching books: " + ex.getMessage());
            }
        });

        // Add enter key handler for book search
        bookSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                bookSearchButton.fire();
            }
        });

        // Create search box for rentals
        HBox rentalSearchBox = new HBox(10);
        TextField rentalSearchField = new TextField();
        rentalSearchField.setPromptText("Search rentals by borrower name...");
        rentalSearchField.setPrefWidth(300);
        Button rentalSearchButton = new Button("Search Rentals");
        rentalSearchBox.getChildren().addAll(new Label("Search Rentals:"), rentalSearchField, rentalSearchButton);

        // Add rental search functionality
        rentalSearchButton.setOnAction(e -> {
            String searchText = rentalSearchField.getText().toLowerCase();
            try {
                List<Rental> allRentals = rentalDAO.getAllActiveRentals();
                rentalTableView.getItems().clear();
                if (searchText.isEmpty()) {
                    rentalTableView.getItems().addAll(allRentals);
                } else {
                    rentalTableView.getItems().addAll(
                        allRentals.stream()
                            .filter(rental -> 
                                rental.getBorrowerName().toLowerCase().contains(searchText))
                            .collect(Collectors.toList())
                    );
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error searching rentals: " + ex.getMessage());
            }
        });

        // Add enter key handler for rental search
        rentalSearchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                rentalSearchButton.fire();
            }
        });

        // Add Rental Management components to rentals tab
        rentalsLayout.getChildren().addAll(
            selectBookLabel,
            bookSearchBox,
            rentalBooksTableView,
            new Label("Rental Details:"),
            rentalForm,
            rentalButtonBox,
            new Label("Active Rentals:"),
            rentalSearchBox,
            rentalTableView
        );

        // Load books in the rental tab's book table
        try {
            rentalBooksTableView.getItems().addAll(bookDAO.getAllBooks());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading books for rental");
        }

        // Update the rent book function to use the rental tab's book table
        rentButton.setOnAction(e -> {
            Book selectedBook = rentalBooksTableView.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a book to rent from the table above.");
                return;
            }
            rentBook(selectedBook);
        });

        // Add tabs to tab pane
        tabPane.getTabs().addAll(booksTab, rentalsTab);
        
        // Add tab pane to main layout
        mainLayout.getChildren().add(tabPane);

        // Event handlers
        addButton.setOnAction(e -> addBook());
        updateButton.setOnAction(e -> updateBook());
        deleteButton.setOnAction(e -> deleteBook());
        clearButton.setOnAction(e -> clearFields());
        returnButton.setOnAction(e -> returnBook());
        refreshBooksButton.setOnAction(e -> refreshTableView());
        
        // Add refresh handler for both book tables in rental tab
        refreshRentalsButton.setOnAction(e -> {
            try {
                // Clear search fields
                bookSearchField.clear();
                rentalSearchField.clear();
                
                // Refresh the books table in rental tab
                rentalBooksTableView.getItems().clear();
                rentalBooksTableView.getItems().addAll(bookDAO.getAllBooks());
                
                // Refresh the rentals table
                refreshRentalTableView();
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Tables refreshed successfully!");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error refreshing tables: " + ex.getMessage());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleField.setText(newSelection.getTitle());
                authorField.setText(newSelection.getAuthor());
                isbnField.setText(newSelection.getIsbn());
                quantityField.setText(String.valueOf(newSelection.getQuantity()));
            }
        });

        // Initial load of books
        refreshTableView();

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addBook() {
        try {
            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            book.setQuantity(Integer.parseInt(quantityField.getText()));
            
            bookDAO.addBook(book);
            refreshTableView();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding book: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number for quantity.");
        }
    }

    private void updateBook() {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a book to update.");
            return;
        }

        try {
            Book book = new Book();
            book.setId(selectedBook.getId());
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            book.setQuantity(Integer.parseInt(quantityField.getText()));
            
            bookDAO.updateBook(book);
            refreshTableView();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating book: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number for quantity.");
        }
    }

    private void deleteBook() {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a book to delete.");
            return;
        }

        try {
            bookDAO.deleteBook(selectedBook.getId());
            refreshTableView();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error deleting book: " + e.getMessage());
        }
    }

    private void refreshTableView() {
        try {
            tableView.getItems().clear();
            tableView.getItems().addAll(bookDAO.getAllBooks());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading books: " + e.getMessage());
        }
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        quantityField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void rentBook(Book selectedBook) {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a book to rent.");
            return;
        }

        if (!selectedBook.isAvailable()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "This book is not available for rent.");
            return;
        }

        if (borrowerNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter borrower name.");
            return;
        }

        if (dueDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a due date.");
            return;
        }

        try {
            Rental rental = new Rental();
            rental.setBookId(selectedBook.getId());
            rental.setBorrowerName(borrowerNameField.getText().trim());
            rental.setBorrowedDate(LocalDate.now());
            rental.setDueDate(dueDatePicker.getValue());
            
            rentalDAO.rentBook(rental);
            refreshTableView();
            refreshRentalTableView();
            clearRentalFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book rented successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error renting book: " + e.getMessage());
        }
    }

    private void returnBook() {
        Rental selectedRental = rentalTableView.getSelectionModel().getSelectedItem();
        if (selectedRental == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a rental to return.");
            return;
        }

        try {
            rentalDAO.returnBook(selectedRental.getId());
            refreshTableView();
            refreshRentalTableView();
            clearRentalFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error returning book: " + e.getMessage());
        }
    }

    private void refreshRentalTableView() {
        try {
            rentalTableView.getItems().clear();
            rentalTableView.getItems().addAll(rentalDAO.getAllActiveRentals());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading rentals: " + e.getMessage());
        }
    }

    private void clearRentalFields() {
        borrowerNameField.clear();
        dueDatePicker.setValue(null);
        rentalTableView.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}