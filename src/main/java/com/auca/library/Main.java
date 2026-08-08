package com.auca.library;

import com.auca.library.dao.*;
import com.auca.library.domain.*;
import com.auca.library.domain.enums.*;
import com.auca.library.service.*;
import com.auca.library.util.PasswordUtil;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        DataSeeder seeder = new DataSeeder();
        seeder.seedMembershipTypes();

        LocationService locationService = new LocationService();

        Location kigali = new Location();
        kigali.setLocationCode("KIG");
        kigali.setLocationName("Kigali City");
        kigali.setLocationType(LocationType.PROVINCE);
        locationService.createLocation(kigali, null);

        Location nyarugenge = new Location();
        nyarugenge.setLocationCode("NYA");
        nyarugenge.setLocationName("Nyarugenge District");
        nyarugenge.setLocationType(LocationType.DISTRICT);
        locationService.createLocation(nyarugenge, kigali.getLocationId());

        Location nyarugengeSector = new Location();
        nyarugengeSector.setLocationCode("NYA-SEC");
        nyarugengeSector.setLocationName("Nyarugenge Sector");
        nyarugengeSector.setLocationType(LocationType.SECTOR);
        locationService.createLocation(nyarugengeSector, nyarugenge.getLocationId());

        Location muhima = new Location();
        muhima.setLocationCode("MUH-CELL");
        muhima.setLocationName("Muhima Cell");
        muhima.setLocationType(LocationType.CELL);
        locationService.createLocation(muhima, nyarugengeSector.getLocationId());

        Location biryogo = new Location();
        biryogo.setLocationCode("BIR-VIL");
        biryogo.setLocationName("Biryogo Village");
        biryogo.setLocationType(LocationType.VILLAGE);
        locationService.createLocation(biryogo, muhima.getLocationId());

        UserDao userDao = new UserDao();

        User librarian = new User();
        librarian.setFirstName("Alice");
        librarian.setLastName("Uwase");
        librarian.setGender(Gender.FEMALE);
        librarian.setPhoneNumber("0780000001");
        librarian.setUserName("alice.librarian");
        librarian.setPassword(PasswordUtil.hash("password123"));
        librarian.setRole(Role.LIBRARIAN);
        librarian.setVillageId(biryogo.getLocationId());
        userDao.save(librarian);

        User student1 = new User();
        student1.setFirstName("Bob");
        student1.setLastName("Mugisha");
        student1.setGender(Gender.MALE);
        student1.setPhoneNumber("0780000002");
        student1.setUserName("bob.student");
        student1.setPassword(PasswordUtil.hash("password123"));
        student1.setRole(Role.STUDENT);
        student1.setVillageId(biryogo.getLocationId());
        userDao.save(student1);

        User student2 = new User();
        student2.setFirstName("Claire");
        student2.setLastName("Ineza");
        student2.setGender(Gender.FEMALE);
        student2.setPhoneNumber("0780000003");
        student2.setUserName("claire.student");
        student2.setPassword(PasswordUtil.hash("password123"));
        student2.setRole(Role.STUDENT);
        student2.setVillageId(biryogo.getLocationId());
        userDao.save(student2);

        RoomDao roomDao = new RoomDao();
        Room room = new Room();
        room.setRoomCode("ROOM-A1");
        roomDao.save(room);

        ShelfService shelfService = new ShelfService();
        ShelfDao shelfDao = new ShelfDao();
        Shelf shelf = new Shelf();
        shelf.setBookCategory("Science");
        shelf.setInitialStock(0);
        shelf.setAvailableStock(0);
        shelf.setBorrowedNumber(0);
        shelfDao.save(shelf);
        shelfService.assignShelfToRoom(shelf.getShelfId(), room.getRoomId());

        BookDao bookDao = new BookDao();

        Book book1 = new Book();
        book1.setTitle("Introduction to Algorithms");
        book1.setIsbnCode("ISBN-001");
        book1.setEdition(3);
        book1.setPublicationYear(LocalDate.of(2009, 1, 1));
        book1.setPublisherName("MIT Press");
        book1.setBookStatus(BookStatus.AVAILABLE);
        bookDao.save(book1);
        shelfService.assignBookToShelf(book1.getBookId(), shelf.getShelfId());

        Book book2 = new Book();
        book2.setTitle("Clean Code");
        book2.setIsbnCode("ISBN-002");
        book2.setEdition(1);
        book2.setPublicationYear(LocalDate.of(2008, 1, 1));
        book2.setPublisherName("Prentice Hall");
        book2.setBookStatus(BookStatus.AVAILABLE);
        bookDao.save(book2);
        shelfService.assignBookToShelf(book2.getBookId(), shelf.getShelfId());

        MembershipTypeDao membershipTypeDao = new MembershipTypeDao();
        MembershipType goldType = membershipTypeDao.findByName("GOLD");

        MembershipService membershipService = new MembershipService();
        Membership membership = membershipService.registerMembership(student1.getPersonId(), goldType.getMembershipTypeId());

        MembershipDao membershipDao = new MembershipDao();
        membership.setMembershipStatus(MembershipStatus.APPROVED);
        membershipDao.update(membership);

        BorrowingService borrowingService = new BorrowingService();
        borrowingService.borrowBook(student1.getPersonId(), book1.getBookId());

        System.out.println("Data seeding complete. Check auca_library_db.");
    }
}
