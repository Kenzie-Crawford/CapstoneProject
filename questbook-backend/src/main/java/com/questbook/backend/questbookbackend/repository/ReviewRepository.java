package com.questbook.backend.questbookbackend.repository;
import com.questbook.backend.questbookbackend.model.Book;
import com.questbook.backend.questbookbackend.model.Review;
import com.questbook.backend.questbookbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface ReviewRepository extends JpaRepository<Review, Integer > {

    List<Review> findByUser(User user);
    List<Review> findByBook(Book book);
    List<Review> findByUserId(int userId);
    List<Review> findByBookId(int bookId);
    Optional<Review> findByUserIdAndBookId(int userId, int bookId);

    @Query("Select AVg(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(int bookId);
}
