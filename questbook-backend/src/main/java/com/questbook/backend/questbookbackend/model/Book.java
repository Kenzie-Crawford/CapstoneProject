package com.questbook.backend.questbookbackend.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "open_library_id", unique = true)
    private String openLibraryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column (nullable = false)
    private String genre;

    @Column (columnDefinition = "TEXT")
    private String description;

    @Column (name = "published_date")
    private LocalDate publishedDate;

    private String source;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_at")
    private LocalDateTime upatedAt;
}
