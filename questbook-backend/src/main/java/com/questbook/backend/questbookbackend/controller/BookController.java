package com.questbook.backend.questbookbackend.controller;

import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Search for books by title or author
     * GET /api/books/search?q=harry potter
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = false) String q) {
        try {
            List<Book> books = bookService.searchBooks(q);

            if (books.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No books found for the given query"));
            }

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search books: " + e.getMessage()));
        }
    }

    /**
     * Get all books
     * GET /api/books
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        try {
            List<Book> books = bookService.searchBooks(null); // null returns all books
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve books"));
        }
    }

    /**
     * Get books by genre
     * GET /api/books/genre/{genre}
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getBooksByGenre(@PathVariable String genre) {
        try {
            // You'll need to add this method to your BookService
            List<Book> books = bookService.getBooksByGenre(genre);

            if (books.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No books found for genre: " + genre));
            }

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve books by genre"));
        }
    }

    /**
     * Get book by ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable int id) {
        try {
            // You'll need to add this method to your BookService
            Book book = bookService.getBookById(id);

            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found with id: " + id));
            }

            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve book"));
        }
    }

    /**
     * Get book by Open Library ID
     * GET /api/books/openlibrary/{openLibraryId}
     */
    @GetMapping("/openlibrary/{openLibraryId}")
    public ResponseEntity<?> getBookByOpenLibraryId(@PathVariable String openLibraryId) {
        try {
            // You'll need to add this method to your BookService
            Book book = bookService.getBookByOpenLibraryId(openLibraryId);

            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found with Open Library ID: " + openLibraryId));
            }

            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve book"));
        }
    }

    /**
     * Save a book to the database
     * POST /api/books
     */
    @PostMapping
    public ResponseEntity<?> saveBook(@RequestBody Book book) {
        try {
            // You'll need to add this method to your BookService
            Book savedBook = bookService.saveBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save book"));
        }
    }

    /**
     * Update an existing book
     * PUT /api/books/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody Book book) {
        try {
            // You'll need to add this method to your BookService
            Book updatedBook = bookService.updateBook(id, book);

            if (updatedBook == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found with id: " + id));
            }

            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update book"));
        }
    }

    /**
     * Delete a book
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id) {
        try {
            // You'll need to add this method to your BookService
            boolean deleted = bookService.deleteBook(id);

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found with id: " + id));
            }

            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete book"));
        }
    }

    /**
     * Import books from Open Library API and save to database
     * POST /api/books/import?q=harry potter
     */
    @PostMapping("/import")
    public ResponseEntity<?> importBooks(@RequestParam String q) {
        try {
            // You'll need to add this method to your BookService
            List<Book> importedBooks = bookService.importBooksFromApi(q);

            if (importedBooks.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No books found to import"));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Successfully imported " + importedBooks.size() + " books",
                            "books", importedBooks
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to import books: " + e.getMessage()));
        }
    }
}
