package com.questbook.backend.questbookbackend.repository;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface BookRepository extends JpaRepository<Book, Integer>{
    Optional<Book> findByTitleContainingIgnoreCase (String Title);
    List<Book> findByAuthorIgnoreCase (String author);
    Optional<Book> findByOpenLibraryId(String openLibraryId);
    List<Book> findByGenreContainingIgnoreCase(String genre);
}
