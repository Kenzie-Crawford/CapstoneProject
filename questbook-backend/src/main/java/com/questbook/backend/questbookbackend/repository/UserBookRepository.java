package com.questbook.backend.questbookbackend.repository;

import com.questbook.backend.questbookbackend.model.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Integer> {

    List<UserBook> findByBookId(int bookId);
    List<UserBook> findByUserId(int userId);

    List<UserBook> findByUser(User user);
    List<UserBook> findByBook(Book book);

    List<UserBook> findByStatus(String status);

    List<UserBook> findByBookTitleContainingIgnoreCase(String title);

    Optional<UserBook> findByUserIdAndBookId(int userId, int bookId);

    int countByBookIdAndStatus(int bookId, String status);
}