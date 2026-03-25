package com.questbook.backend.questbookbackend.service;

import org.springframework.web.client.RestTemplate;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }

        List<Book> results = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);

        if (results.isEmpty()) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = "https://openlibrary.org/search.json?q=" +
                        UriUtils.encode(query, StandardCharsets.UTF_8);

                Map response = restTemplate.getForObject(url, Map.class);

                if (response == null || response.get("docs") == null) {
                    return new ArrayList<>(); // fallback
                }

                List<Map<String, Object>> docs =
                        (List<Map<String, Object>>) response.get("docs");

                List<Book> apiResults = new ArrayList<>();

                for (Map<String, Object> doc : docs) {

                    Book book = new Book();

                    book.setTitle((String) doc.getOrDefault("title", "Unknown Title"));

                    List<String> authors = (List<String>) doc.get("author_name");
                    if (authors != null && !authors.isEmpty()) {
                        book.setAuthor(authors.get(0));
                    } else {
                        book.setAuthor("Unknown");
                    }

                    book.setGenre("Unknown");
                    book.setSource("API");

                    apiResults.add(book);
                }

                return apiResults;

            } catch (Exception e) {
                System.out.println("API FAILED: " + e.getMessage());

                // fallback when API is down
                return new ArrayList<>();
            }
        }

        return results;
    }
}

