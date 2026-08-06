package com.auca.library.service;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.Shelf;

import java.util.UUID;

public class ShelfService {

    private final BookDao bookDao = new BookDao();
    private final ShelfDao shelfDao = new ShelfDao();

    public void assignBookToShelf(UUID bookId, UUID shelfId) {
        Book book = bookDao.findById(bookId);
        if (book == null) throw new IllegalArgumentException("Book not found");
        Shelf shelf = shelfDao.findById(shelfId);
        if (shelf == null) throw new IllegalArgumentException("Shelf not found");

        book.setShelfId(shelfId);
        bookDao.update(book);

        shelf.setAvailableStock(shelf.getAvailableStock() + 1);
        shelfDao.update(shelf);
    }

    public void assignShelfToRoom(UUID shelfId, UUID roomId) {
        Shelf shelf = shelfDao.findById(shelfId);
        if (shelf == null) throw new IllegalArgumentException("Shelf not found");
        shelf.setRoomId(roomId);
        shelfDao.update(shelf);
    }
}
