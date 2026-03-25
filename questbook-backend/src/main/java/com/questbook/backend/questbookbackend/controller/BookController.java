package com.questbook.backend.questbookbackend.controller;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.service.BookService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/books")
@CrossOrigin(origins = "*")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(required = false) String q){
        return bookService.searchBooks(q);
    }
}
