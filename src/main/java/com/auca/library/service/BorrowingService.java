package com.auca.library.service;

import com.auca.library.dao.BookDao;
import com.auca.library.dao.BorrowerDao;
import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.ShelfDao;
import com.auca.library.domain.Book;
import com.auca.library.domain.Borrower;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.Shelf;
import com.auca.library.domain.enums.BookStatus;
import com.auca.library.exception.BorrowLimitExceededException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BorrowingService {

    // The standard loan window for every borrow transaction
    private static final int LOAN_PERIOD_DAYS = 14;

    private final BorrowerDao borrowerDao = new BorrowerDao();
    private final BookDao bookDao = new BookDao();
    private final ShelfDao shelfDao = new ShelfDao();
    private final MembershipDao membershipDao = new MembershipDao();
    private final MembershipTypeDao membershipTypeDao = new MembershipTypeDao();

    public Borrower borrowBook(UUID readerId, UUID bookId) {
        validateBorrowLimit(readerId);

        Book book = bookDao.findById(bookId);
        if (book == null || book.getBookStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        Borrower borrower = new Borrower();
        borrower.setReaderId(readerId);
        borrower.setBookId(bookId);
        borrower.setPickupDate(LocalDate.now());
        borrower.setDueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        borrower.setFine(0);
        borrowerDao.save(borrower);

        book.setBookStatus(BookStatus.BORROWED);
        bookDao.update(book);

        if (book.getShelfId() != null) {
            Shelf shelf = shelfDao.findById(book.getShelfId());
            if (shelf != null) {
                shelf.setAvailableStock(shelf.getAvailableStock() - 1);
                shelf.setBorrowedNumber(shelf.getBorrowedNumber() + 1);
                shelfDao.update(shelf);
            }
        }

        return borrower;
    }

    public void validateBorrowLimit(UUID readerId) {
        Membership membership = membershipDao.findApprovedByReaderId(readerId);
        if (membership == null) {
            throw new BorrowLimitExceededException("No approved membership found for reader");
        }
        MembershipType membershipType = membershipTypeDao.findById(membership.getMembershipTypeId());
        int activeBorrows = borrowerDao.findActiveByReaderId(readerId).size();
        if (activeBorrows >= membershipType.getMaxBooks()) {
            throw new BorrowLimitExceededException("Borrow limit of " + membershipType.getMaxBooks() + " reached");
        }
    }

    public int calculateLateFee(UUID borrowerId) {
        Borrower borrower = borrowerDao.findById(borrowerId);
        Membership membership = membershipDao.findApprovedByReaderId(borrower.getReaderId());
        if (membership == null) return 0;
        MembershipType membershipType = membershipTypeDao.findById(membership.getMembershipTypeId());

        LocalDate endDate = borrower.getReturnDate() != null ? borrower.getReturnDate() : LocalDate.now();
        long daysLate = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(borrower.getDueDate(), endDate));
        return (int) (daysLate * membershipType.getPrice());
    }
}
