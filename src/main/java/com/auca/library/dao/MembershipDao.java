package com.auca.library.dao;

import com.auca.library.domain.Membership;
import com.auca.library.domain.enums.MembershipStatus;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.UUID;

public class MembershipDao {

    public void save(Membership membership) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(membership);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Membership findById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Membership.class, id);
        }
    }

    public void update(Membership membership) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(membership);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<Membership> findActiveByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Membership WHERE readerId = :readerId AND (membershipStatus = :approved OR membershipStatus = :pending)", Membership.class)
                    .setParameter("readerId", readerId)
                    .setParameter("approved", MembershipStatus.APPROVED)
                    .setParameter("pending", MembershipStatus.PENDING)
                    .list();
        }
    }

    public Membership findApprovedByReaderId(UUID readerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Membership WHERE readerId = :readerId AND membershipStatus = :approved", Membership.class)
                    .setParameter("readerId", readerId)
                    .setParameter("approved", MembershipStatus.APPROVED)
                    .uniqueResult();
        }
    }
}
