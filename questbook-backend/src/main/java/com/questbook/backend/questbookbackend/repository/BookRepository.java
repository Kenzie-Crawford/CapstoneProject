package com.questbook.backend.questbookbackend.repository;
import com.questbook.backend.questbookbackend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface BookRepository extends JpaRepository<Book, Integer>{
    List<Book> findByTitleContainingIgnoreCase (String title);
    List<Book> findByAuthorContainingIgnoreCase (String author);
    Optional<Book> findByOpenLibraryId(String openLibraryId);
    List<Book> findByGenreContainingIgnoreCase(String genre);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
