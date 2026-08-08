package com.auca.library;

import com.auca.library.dao.*;
import com.auca.library.domain.*;
import com.auca.library.domain.enums.*;
import com.auca.library.exception.BorrowLimitExceededException;
import com.auca.library.service.BorrowingService;
import com.auca.library.util.HibernateUtil;
import com.auca.library.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class BorrowingServiceTest {

    private BorrowingService borrowingService;
    private UserDao userDao;
    private BookDao bookDao;
    private BorrowerDao borrowerDao;
    private MembershipDao membershipDao;
    private MembershipTypeDao membershipTypeDao;
    private ShelfDao shelfDao;

    private User goldUser;
    private User silverUser;
    private User striverUser;
    private User noMembershipUser;
    private MembershipType goldType;
    private MembershipType silverType;
    private MembershipType striverType;
    private Shelf shelf;

    private final List<UUID> createdBookIds = new ArrayList<>();
    private final List<UUID> createdBorrowerIds = new ArrayList<>();
    private final List<UUID> createdMembershipIds = new ArrayList<>();
    private final List<UUID> createdUserIds = new ArrayList<>();
    private UUID shelfId;

    @Before
    public void setUp() {
        borrowingService = new BorrowingService();
        userDao = new UserDao();
        bookDao = new BookDao();
        borrowerDao = new BorrowerDao();
        membershipDao = new MembershipDao();
        membershipTypeDao = new MembershipTypeDao();
        shelfDao = new ShelfDao();

        goldType = membershipTypeDao.findByName("GOLD");
        if (goldType == null) {
            goldType = new MembershipType();
            goldType.setMembershipName("GOLD");
            goldType.setPrice(50);
            goldType.setMaxBooks(5);
            membershipTypeDao.save(goldType);
        }

        silverType = membershipTypeDao.findByName("SILVER");
        if (silverType == null) {
            silverType = new MembershipType();
            silverType.setMembershipName("SILVER");
            silverType.setPrice(30);
            silverType.setMaxBooks(3);
            membershipTypeDao.save(silverType);
        }

        striverType = membershipTypeDao.findByName("STRIVER");
        if (striverType == null) {
            striverType = new MembershipType();
            striverType.setMembershipName("STRIVER");
            striverType.setPrice(10);
            striverType.setMaxBooks(2);
            membershipTypeDao.save(striverType);
        }

        shelf = new Shelf();
        shelf.setBookCategory("Test");
        shelf.setInitialStock(0);
        shelf.setAvailableStock(10);
        shelf.setBorrowedNumber(0);
        shelfDao.save(shelf);
        shelfId = shelf.getShelfId();

        goldUser = createUser("gold-" + UUID.randomUUID(), goldType);
        silverUser = createUser("silver-" + UUID.randomUUID(), silverType);
        striverUser = createUser("striver-" + UUID.randomUUID(), striverType);
        noMembershipUser = createUserNoMembership("nomem-" + UUID.randomUUID());
    }

    private User createUser(String username, MembershipType type) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender(Gender.MALE);
        user.setPhoneNumber("078000000");
        user.setUserName(username);
        user.setPassword(PasswordUtil.hash("pass"));
        user.setRole(Role.STUDENT);
        userDao.save(user);
        createdUserIds.add(user.getPersonId());

        Membership m = new Membership();
        m.setMembershipCode(UUID.randomUUID().toString());
        m.setReaderId(user.getPersonId());
        m.setMembershipTypeId(type.getMembershipTypeId());
        m.setMembershipStatus(MembershipStatus.APPROVED);
        m.setRegistrationDate(LocalDate.now());
        membershipDao.save(m);
        createdMembershipIds.add(m.getMembershipId());

        return user;
    }

    private User createUserNoMembership(String username) {
        User user = new User();
        user.setFirstName("No");
        user.setLastName("Membership");
        user.setGender(Gender.MALE);
        user.setPhoneNumber("078000000");
        user.setUserName(username);
        user.setPassword(PasswordUtil.hash("pass"));
        user.setRole(Role.STUDENT);
        userDao.save(user);
        createdUserIds.add(user.getPersonId());
        return user;
    }

    private Book createAvailableBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbnCode("ISBN-" + UUID.randomUUID());
        book.setEdition(1);
        book.setPublicationYear(LocalDate.of(2020, 1, 1));
        book.setPublisherName("Test Publisher");
        book.setBookStatus(BookStatus.AVAILABLE);
        book.setShelfId(shelfId);
        bookDao.save(book);
        createdBookIds.add(book.getBookId());
        return book;
    }

    private Borrower createActiveBorrow(UUID readerId) {
        Book book = createAvailableBook();
        Borrower b = new Borrower();
        b.setReaderId(readerId);
        b.setBookId(book.getBookId());
        b.setPickupDate(LocalDate.now());
        b.setDueDate(LocalDate.now().plusDays(14));
        b.setFine(0);
        borrowerDao.save(b);
        createdBorrowerIds.add(b.getBorrowId());
        book.setBookStatus(BookStatus.BORROWED);
        bookDao.update(book);
        return b;
    }

    @After
    public void tearDown() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        for (UUID id : createdBorrowerIds) {
            Borrower b = session.get(Borrower.class, id);
            if (b != null) session.remove(b);
        }
        for (UUID id : createdBookIds) {
            Book b = session.get(Book.class, id);
            if (b != null) session.remove(b);
        }
        for (UUID id : createdMembershipIds) {
            Membership m = session.get(Membership.class, id);
            if (m != null) session.remove(m);
        }
        for (UUID id : createdUserIds) {
            User u = session.get(User.class, id);
            if (u != null) session.remove(u);
        }
        Shelf s = session.get(Shelf.class, shelfId);
        if (s != null) session.remove(s);
        tx.commit();
        session.close();
    }

    @Test
    public void borrowBook_availableBook_createsBorrowerRecordWithZeroFine() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        assertNotNull(borrower.getBorrowId());
        assertEquals(0, borrower.getFine());
    }

    @Test
    public void borrowBook_setsBookStatusToBorrowed() {
        Book book = createAvailableBook();
        borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        Book updated = bookDao.findById(book.getBookId());
        assertEquals(BookStatus.BORROWED, updated.getBookStatus());
    }

    @Test
    public void borrowBook_dueDateIsPickupDatePlusLoanPeriod() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        assertEquals(borrower.getPickupDate().plusDays(14), borrower.getDueDate());
    }

    @Test
    public void goldMember_withFourActiveBorrows_canBorrowAFifth() {
        for (int i = 0; i < 4; i++) createActiveBorrow(goldUser.getPersonId());
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        assertNotNull(borrower.getBorrowId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void goldMember_withFiveActiveBorrows_cannotBorrowASixth() {
        for (int i = 0; i < 5; i++) createActiveBorrow(goldUser.getPersonId());
        Book book = createAvailableBook();
        borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void silverMember_withThreeActiveBorrows_isBlocked() {
        for (int i = 0; i < 3; i++) createActiveBorrow(silverUser.getPersonId());
        Book book = createAvailableBook();
        borrowingService.borrowBook(silverUser.getPersonId(), book.getBookId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void striverMember_withTwoActiveBorrows_isBlocked() {
        for (int i = 0; i < 2; i++) createActiveBorrow(striverUser.getPersonId());
        Book book = createAvailableBook();
        borrowingService.borrowBook(striverUser.getPersonId(), book.getBookId());
    }

    @Test(expected = BorrowLimitExceededException.class)
    public void userWithoutApprovedMembership_isBlocked() {
        Book book = createAvailableBook();
        borrowingService.borrowBook(noMembershipUser.getPersonId(), book.getBookId());
    }

    @Test
    public void returnedOnDueDate_feeIsZero() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        borrower.setReturnDate(borrower.getDueDate());
        borrowerDao.update(borrower);
        assertEquals(0, borrowingService.calculateLateFee(borrower.getBorrowId()));
    }

    @Test
    public void goldMember_returnedThreeDaysLate_feeIs150() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        // Gold: 3 days late × 50 Rwf/day = 150
        borrower.setReturnDate(borrower.getDueDate().plusDays(3));
        borrowerDao.update(borrower);
        assertEquals(150, borrowingService.calculateLateFee(borrower.getBorrowId()));
    }

    @Test
    public void silverMember_returnedFiveDaysLate_feeIs150() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(silverUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        // Silver: 5 days late × 30 Rwf/day = 150
        borrower.setReturnDate(borrower.getDueDate().plusDays(5));
        borrowerDao.update(borrower);
        assertEquals(150, borrowingService.calculateLateFee(borrower.getBorrowId()));
    }

    @Test
    public void striverMember_returnedOneDayLate_feeIs10() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(striverUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        // Striver: 1 day late × 10 Rwf/day = 10
        borrower.setReturnDate(borrower.getDueDate().plusDays(1));
        borrowerDao.update(borrower);
        assertEquals(10, borrowingService.calculateLateFee(borrower.getBorrowId()));
    }

    @Test
    public void notYetReturned_feeIsComputedAgainstToday() {
        Book book = createAvailableBook();
        Borrower borrower = borrowingService.borrowBook(goldUser.getPersonId(), book.getBookId());
        createdBorrowerIds.add(borrower.getBorrowId());
        // Manually set dueDate to 2 days ago so it's overdue
        borrower.setDueDate(LocalDate.now().minusDays(2));
        borrowerDao.update(borrower);
        int fee = borrowingService.calculateLateFee(borrower.getBorrowId());
        // 2 days late × 50 Rwf/day = 100
        assertEquals(100, fee);
    }
}
