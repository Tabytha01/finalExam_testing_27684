package com.auca.library.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "borrowers")
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "borrow_id")
    private UUID borrowId;

    @Column(name = "reader_id")
    private UUID readerId;

    @Column(name = "book_id")
    private UUID bookId;

    private LocalDate pickupDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private int fine;
    private int lateChargeFees;

    public UUID getBorrowId() { return borrowId; }
    public void setBorrowId(UUID borrowId) { this.borrowId = borrowId; }
    public UUID getReaderId() { return readerId; }
    public void setReaderId(UUID readerId) { this.readerId = readerId; }
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }
    public int getLateChargeFees() { return lateChargeFees; }
    public void setLateChargeFees(int lateChargeFees) { this.lateChargeFees = lateChargeFees; }
}
