package com.auca.library.service;

import com.auca.library.dao.RoomDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Room;
import com.auca.library.domain.Shelf;

import java.util.List;
import java.util.UUID;

public class RoomService {

    private final RoomDao roomDao = new RoomDao();
    private final ShelfDao shelfDao = new ShelfDao();

    public int countBooksInRoom(UUID roomId) {
        List<Shelf> shelves = shelfDao.findByRoomId(roomId);
        return shelves.stream()
                .mapToInt(s -> s.getAvailableStock() + s.getBorrowedNumber())
                .sum();
    }

    public Room findRoomWithFewestBooks() {
        List<Room> rooms = roomDao.findAll();
        Room fewest = null;
        int min = Integer.MAX_VALUE;
        for (Room room : rooms) {
            int count = countBooksInRoom(room.getRoomId());
            if (count < min) {
                min = count;
                fewest = room;
            }
        }
        return fewest;
    }
}
