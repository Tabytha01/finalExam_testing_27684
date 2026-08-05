package com.auca.library.domain;

import com.auca.library.domain.enums.BookStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "book_id")
    private UUID bookId;

    private String title;

    @Column(unique = true)
    private String isbnCode;

    private int edition;

    private LocalDate publicationYear;

    private String publisherName;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Column(name = "shelf_id")
    private UUID shelfId;

    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbnCode() { return isbnCode; }
    public void setIsbnCode(String isbnCode) { this.isbnCode = isbnCode; }
    public int getEdition() { return edition; }
    public void setEdition(int edition) { this.edition = edition; }
    public LocalDate getPublicationYear() { return publicationYear; }
    public void setPublicationYear(LocalDate publicationYear) { this.publicationYear = publicationYear; }
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    public BookStatus getBookStatus() { return bookStatus; }
    public void setBookStatus(BookStatus bookStatus) { this.bookStatus = bookStatus; }
    public UUID getShelfId() { return shelfId; }
    public void setShelfId(UUID shelfId) { this.shelfId = shelfId; }
}
