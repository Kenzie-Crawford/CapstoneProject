package com.questbook.backend.questbookbackend.service;

import org.springframework.web.client.RestTemplate;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    private static final List<String> GENRE_KEYWORDS = Arrays.asList(
            "fiction", "fantasy", "science fiction", "mystery",
            "thriller", "romance", "horror", "biography", "autobiography",
            "history", "poetry", "drama", "comedy", "adventure",
            "children", "young adult", "crime", "suspense", "classic",
            "historical fiction", "non-fiction", "self-help", "cookbook"
    );

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }

        List<Book> results = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);

        if (results.isEmpty()) {
            try {
                return fetchBooksFromOpenLibrary(query);
            } catch (Exception e) {
                System.out.println("API failed: " + e.getMessage());
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private List<Book> fetchBooksFromOpenLibrary(String query) {
        String searchUrl = "https://openlibrary.org/search.json?q=" +
                UriUtils.encode(query, StandardCharsets.UTF_8) + "&limit=10";

        Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);

        if (searchResponse == null || searchResponse.get("docs") == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> docs = (List<Map<String, Object>>) searchResponse.get("docs");
        List<Book> apiResults = new ArrayList<>();

        for (Map<String, Object> doc : docs) {
            Book book = mapSearchResultToBook(doc);
            apiResults.add(book);
        }

        return apiResults;
    }

    @SuppressWarnings("unchecked")
    private Book mapSearchResultToBook(Map<String, Object> searchDoc) {
        Book book = new Book();

        // Basic info from search results
        book.setTitle((String) searchDoc.getOrDefault("title", "Unknown Title"));

        List<String> authors = (List<String>) searchDoc.get("author_name");
        if (authors != null && !authors.isEmpty()) {
            book.setAuthor(authors.get(0));
        } else {
            book.setAuthor("Unknown");
        }

        String workKey = (String) searchDoc.get("key");
        if (workKey != null) {
            book.setOpenLibraryId(workKey);
        }

        Integer year = (Integer) searchDoc.get("first_publish_year");
        if (year != null) {
            try {
                book.setPublishedDate(LocalDate.of(year, 1, 1));
            } catch (Exception e) {
                // Invalid date, skip
            }
        }

        // Fetch detailed information (genre and description)
        fetchBookDetails(workKey, book);

        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setSource("API");

        return book;
    }

    @SuppressWarnings("unchecked")
    private void fetchBookDetails(String workKey, Book book) {
        if (workKey == null) {
            book.setGenre("Unknown");
            return;
        }

        try {
            String detailsUrl = "https://openlibrary.org" + workKey + ".json";
            System.out.println("Fetching details from: " + detailsUrl);

            Map<String, Object> workDetails = restTemplate.getForObject(detailsUrl, Map.class);

            if (workDetails == null) {
                System.out.println("No work details found for: " + workKey);
                book.setGenre("Unknown");
                return;
            }

            // Extract genre from subjects
            String genre = extractGenreFromWorkDetails(workDetails);
            book.setGenre(genre);

            // Extract description
            String description = extractDescriptionFromWorkDetails(workDetails);
            if (description != null && !description.isEmpty()) {
                book.setDescription(description);
                System.out.println("Found description: " + description.substring(0, Math.min(100, description.length())) + "...");
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch work details: " + e.getMessage());
            book.setGenre("Unknown");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractGenreFromWorkDetails(Map<String, Object> workDetails) {
        List<String> subjects = null;

        Object subjectsObj = workDetails.get("subjects");
        if (subjectsObj instanceof List) {
            subjects = (List<String>) subjectsObj;
            System.out.println("Found " + subjects.size() + " subjects in work details");
        }

        if (subjects == null || subjects.isEmpty()) {
            System.out.println("No subjects found in work details");
            return "Unknown";
        }

        return extractGenreFromSubjects(subjects);
    }

    @SuppressWarnings("unchecked")
    private String extractDescriptionFromWorkDetails(Map<String, Object> workDetails) {
        Object descriptionObj = workDetails.get("description");

        if (descriptionObj == null) {
            return null;
        }

        if (descriptionObj instanceof String) {
            return (String) descriptionObj;
        } else if (descriptionObj instanceof Map) {
            Map<String, Object> descriptionMap = (Map<String, Object>) descriptionObj;
            Object value = descriptionMap.get("value");
            if (value != null) {
                return value.toString();
            }
        }

        return null;
    }

    private String extractGenreFromSubjects(List<String> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return "Unknown";
        }

        for (String subject : subjects) {
            String lowerSubject = subject.toLowerCase();
            for (String genreKeyword : GENRE_KEYWORDS) {
                if (lowerSubject.contains(genreKeyword)) {
                    return formatGenre(subject);
                }
            }
        }

        for (String subject : subjects) {
            if (subject.toLowerCase().contains("fiction")) {
                return formatGenre(subject);
            }
        }

        return formatGenre(subjects.get(0));
    }

    private String formatGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            return "Unknown";
        }

        String cleaned = genre
                .replaceAll("(?i)^fiction[/ ]", "")
                .replaceAll("(?i)^juvenile fiction[/ ]", "")
                .replaceAll("(?i)^juvenile works[/ ]", "")
                .replaceAll("(?i)^young adult fiction[/ ]", "")
                .replaceAll("\\s*\\(.*?\\)", "")
                .replaceAll("&", "and")
                .trim();

        if (cleaned.isEmpty()) {
            cleaned = genre;
        }

        String[] words = cleaned.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (capitalized.length() > 0) {
                    capitalized.append(" ");
                }
                if (word.contains("-")) {
                    String[] hyphenParts = word.split("-");
                    for (int i = 0; i < hyphenParts.length; i++) {
                        if (i > 0) capitalized.append("-");
                        if (!hyphenParts[i].isEmpty()) {
                            capitalized.append(Character.toUpperCase(hyphenParts[i].charAt(0)))
                                    .append(hyphenParts[i].substring(1).toLowerCase());
                        }
                    }
                } else {
                    capitalized.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase());
                }
            }
        }

        return capitalized.toString();
    }

    // All other methods (getBookById, saveBook, etc.) remain the same...
    public Book getBookById(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book getBookByOpenLibraryId(String openLibraryId) {
        if (openLibraryId == null) {
            return null;
        }
        return bookRepository.findByOpenLibraryId(openLibraryId).orElse(null);
    }

    public List<Book> getBooksByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return bookRepository.findByGenreContainingIgnoreCase(genre);
    }

    public Book saveBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            book.setGenre("Unknown");
        }

        if (book.getOpenLibraryId() != null) {
            Optional<Book> existingBook = bookRepository.findByOpenLibraryId(book.getOpenLibraryId());
            if (existingBook.isPresent()) {
                throw new IllegalArgumentException("Book already exists with Open Library ID: " + book.getOpenLibraryId());
            }
        }

        if (book.getCreatedAt() == null) {
            book.setCreatedAt(LocalDateTime.now());
        }
        if (book.getUpdatedAt() == null) {
            book.setUpdatedAt(LocalDateTime.now());
        }

        return bookRepository.save(book);
    }

    public Book updateBook(int id, Book bookDetails) {
        Book existingBook = bookRepository.findById(id).orElse(null);

        if (existingBook == null) {
            return null;
        }

        if (bookDetails.getTitle() != null && !bookDetails.getTitle().trim().isEmpty()) {
            existingBook.setTitle(bookDetails.getTitle());
        }
        if (bookDetails.getAuthor() != null && !bookDetails.getAuthor().trim().isEmpty()) {
            existingBook.setAuthor(bookDetails.getAuthor());
        }
        if (bookDetails.getGenre() != null && !bookDetails.getGenre().trim().isEmpty()) {
            existingBook.setGenre(bookDetails.getGenre());
        }
        if (bookDetails.getDescription() != null) {
            existingBook.setDescription(bookDetails.getDescription());
        }
        if (bookDetails.getPublishedDate() != null) {
            existingBook.setPublishedDate(bookDetails.getPublishedDate());
        }
        if (bookDetails.getOpenLibraryId() != null) {
            existingBook.setOpenLibraryId(bookDetails.getOpenLibraryId());
        }

        existingBook.setUpdatedAt(LocalDateTime.now());

        return bookRepository.save(existingBook);
    }

    public boolean deleteBook(int id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Book> importBooksFromApi(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query is required");
        }

        List<Book> booksFromApi = searchBooks(query);
        List<Book> savedBooks = new ArrayList<>();

        for (Book book : booksFromApi) {
            try {
                if (book.getOpenLibraryId() != null) {
                    Optional<Book> existingBook = bookRepository.findByOpenLibraryId(book.getOpenLibraryId());
                    if (existingBook.isPresent()) {
                        continue;
                    }
                }

                Book savedBook = bookRepository.save(book);
                savedBooks.add(savedBook);
            } catch (Exception e) {
                System.err.println("Failed to save book: " + book.getTitle() + " - " + e.getMessage());
            }
        }

        return savedBooks;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> getBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public long countBooks() {
        return bookRepository.count();
    }
}