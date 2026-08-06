package com.auca.library.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "shelves")
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shelf_id")
    private UUID shelfId;

    private String bookCategory;
    private int initialStock;
    private int availableStock;
    private int borrowedNumber;

    @Column(name = "room_id")
    private UUID roomId;

    public UUID getShelfId() { return shelfId; }
    public void setShelfId(UUID shelfId) { this.shelfId = shelfId; }
    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }
    public int getInitialStock() { return initialStock; }
    public void setInitialStock(int initialStock) { this.initialStock = initialStock; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
    public int getBorrowedNumber() { return borrowedNumber; }
    public void setBorrowedNumber(int borrowedNumber) { this.borrowedNumber = borrowedNumber; }
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
}
