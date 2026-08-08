package com.auca.library;

import com.auca.library.dao.MembershipDao;
import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.dao.UserDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.MembershipType;
import com.auca.library.domain.User;
import com.auca.library.domain.enums.Gender;
import com.auca.library.domain.enums.MembershipStatus;
import com.auca.library.domain.enums.Role;
import com.auca.library.exception.ActiveMembershipExistsException;
import com.auca.library.service.MembershipService;
import com.auca.library.util.HibernateUtil;
import com.auca.library.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class MembershipServiceTest {

    private MembershipService membershipService;
    private UserDao userDao;
    private MembershipDao membershipDao;
    private MembershipTypeDao membershipTypeDao;
    private User testUser;
    private MembershipType goldType;

    @Before
    public void setUp() {
        membershipService = new MembershipService();
        userDao = new UserDao();
        membershipDao = new MembershipDao();
        membershipTypeDao = new MembershipTypeDao();

        goldType = membershipTypeDao.findByName("GOLD");
        if (goldType == null) {
            goldType = new MembershipType();
            goldType.setMembershipName("GOLD");
            goldType.setPrice(50);
            goldType.setMaxBooks(5);
            membershipTypeDao.save(goldType);
        }

        testUser = new User();
        testUser.setFirstName("Mem");
        testUser.setLastName("Tester");
        testUser.setGender(Gender.MALE);
        testUser.setPhoneNumber("0780000088");
        testUser.setUserName("memtester-" + UUID.randomUUID());
        testUser.setPassword(PasswordUtil.hash("pass"));
        testUser.setRole(Role.STUDENT);
        userDao.save(testUser);
    }

    @After
    public void tearDown() {
        List<Membership> memberships = membershipDao.findActiveByReaderId(testUser.getPersonId());
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        for (Membership m : memberships) {
            session.remove(session.merge(m));
        }
        session.remove(session.merge(testUser));
        tx.commit();
        session.close();
    }

    @Test
    public void registerMembership_gold_createsPendingMembershipLinkedToGoldType() {
        Membership membership = membershipService.registerMembership(testUser.getPersonId(), goldType.getMembershipTypeId());
        assertNotNull(membership.getMembershipId());
        assertEquals(MembershipStatus.PENDING, membership.getMembershipStatus());
        assertEquals(goldType.getMembershipTypeId(), membership.getMembershipTypeId());
    }

    @Test(expected = ActiveMembershipExistsException.class)
    public void registerMembership_userAlreadyHasActiveMembership_throwsException() {
        membershipService.registerMembership(testUser.getPersonId(), goldType.getMembershipTypeId());
        membershipService.registerMembership(testUser.getPersonId(), goldType.getMembershipTypeId());
    }
}
