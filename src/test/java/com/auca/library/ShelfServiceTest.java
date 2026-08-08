package com.auca.library;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.enums.BookStatus;
import com.auca.library.service.ShelfService;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

public class ShelfServiceTest {

    private ShelfService shelfService;
    private BookDao bookDao;
    private ShelfDao shelfDao;
    private RoomDao roomDao;
    private Book testBook;
    private Shelf testShelf;
    private Room testRoom;

    @Before
    public void setUp() {
        shelfService = new ShelfService();
        bookDao = new BookDao();
        shelfDao = new ShelfDao();
        roomDao = new RoomDao();

        testShelf = new Shelf();
        testShelf.setBookCategory("Test Category");
        testShelf.setInitialStock(0);
        testShelf.setAvailableStock(0);
        testShelf.setBorrowedNumber(0);
        shelfDao.save(testShelf);

        testRoom = new Room();
        testRoom.setRoomCode("ROOM-" + UUID.randomUUID());
        roomDao.save(testRoom);

        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setIsbnCode("ISBN-" + UUID.randomUUID());
        testBook.setEdition(1);
        testBook.setPublicationYear(LocalDate.of(2020, 1, 1));
        testBook.setPublisherName("Test Publisher");
        testBook.setBookStatus(BookStatus.AVAILABLE);
        bookDao.save(testBook);
    }

    @After
    public void tearDown() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Book b = session.get(Book.class, testBook.getBookId());
        if (b != null) session.remove(b);
        Shelf s = session.get(Shelf.class, testShelf.getShelfId());
        if (s != null) session.remove(s);
        Room r = session.get(Room.class, testRoom.getRoomId());
        if (r != null) session.remove(r);
        tx.commit();
        session.close();
    }

    @Test
    public void assignBookToShelf_updatesBookShelfId() {
        shelfService.assignBookToShelf(testBook.getBookId(), testShelf.getShelfId());
        Book updated = bookDao.findById(testBook.getBookId());
        assertEquals(testShelf.getShelfId(), updated.getShelfId());
    }

    @Test
    public void assignBookToShelf_incrementsShelfAvailableStock() {
        int before = testShelf.getAvailableStock();
        shelfService.assignBookToShelf(testBook.getBookId(), testShelf.getShelfId());
        Shelf updated = shelfDao.findById(testShelf.getShelfId());
        assertEquals(before + 1, updated.getAvailableStock());
    }

    @Test
    public void assignShelfToRoom_updatesShelfRoomId() {
        shelfService.assignShelfToRoom(testShelf.getShelfId(), testRoom.getRoomId());
        Shelf updated = shelfDao.findById(testShelf.getShelfId());
        assertEquals(testRoom.getRoomId(), updated.getRoomId());
    }
}
