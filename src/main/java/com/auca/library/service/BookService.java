package com.auca.library.service;

import com.auca.library.dao.BookDao;
import com.auca.library.domain.Book;

import java.util.UUID;

public class BookService {

    private final BookDao bookDao = new BookDao();

    public Book findById(UUID bookId) {
        return bookDao.findById(bookId);
    }

    public void save(Book book) {
        bookDao.save(book);
    }
}
