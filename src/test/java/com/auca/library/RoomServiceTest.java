package com.auca.library;

import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;
import com.auca.library.service.RoomService;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class RoomServiceTest {

    private RoomService roomService;
    private RoomDao roomDao;
    private ShelfDao shelfDao;

    private final List<UUID> createdRoomIds = new ArrayList<>();
    private final List<UUID> createdShelfIds = new ArrayList<>();

    @Before
    public void setUp() {
        roomService = new RoomService();
        roomDao = new RoomDao();
        shelfDao = new ShelfDao();
    }

    @After
    public void tearDown() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        for (UUID id : createdShelfIds) {
            Shelf s = session.get(Shelf.class, id);
            if (s != null) session.remove(s);
        }
        for (UUID id : createdRoomIds) {
            Room r = session.get(Room.class, id);
            if (r != null) session.remove(r);
        }
        tx.commit();
        session.close();
    }

    private Room createRoom() {
        Room room = new Room();
        room.setRoomCode("ROOM-" + UUID.randomUUID());
        roomDao.save(room);
        createdRoomIds.add(room.getRoomId());
        return room;
    }

    private Shelf createShelf(UUID roomId, int available, int borrowed) {
        Shelf shelf = new Shelf();
        shelf.setBookCategory("Test");
        shelf.setInitialStock(0);
        shelf.setAvailableStock(available);
        shelf.setBorrowedNumber(borrowed);
        shelf.setRoomId(roomId);
        shelfDao.save(shelf);
        createdShelfIds.add(shelf.getShelfId());
        return shelf;
    }

    @Test
    public void roomWithMultipleShelves_sumsBookCountsAcrossShelves() {
        Room room = createRoom();
        createShelf(room.getRoomId(), 3, 2);
        createShelf(room.getRoomId(), 1, 4);
        assertEquals(10, roomService.countBooksInRoom(room.getRoomId()));
    }

    @Test
    public void roomWithNoShelves_returnsZero() {
        Room room = createRoom();
        assertEquals(0, roomService.countBooksInRoom(room.getRoomId()));
    }

    @Test
    public void multipleRooms_returnsRoomWithLowestBookCount() {
        Room room1 = createRoom();
        Room room2 = createRoom();
        Room room3 = createRoom();

        createShelf(room1.getRoomId(), 5, 5);
        createShelf(room2.getRoomId(), 1, 1);
        createShelf(room3.getRoomId(), 3, 3);

        Room fewest = roomService.findRoomWithFewestBooks();
        assertNotNull(fewest);
        assertEquals(room2.getRoomId(), fewest.getRoomId());
    }
}
