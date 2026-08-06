package com.auca.library.dao;

import com.auca.library.domain.MembershipType;
import com.auca.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.UUID;

public class MembershipTypeDao {

    public void save(MembershipType membershipType) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(membershipType);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public MembershipType findById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(MembershipType.class, id);
        }
    }

    public MembershipType findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM MembershipType WHERE membershipName = :name", MembershipType.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    public List<MembershipType> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM MembershipType", MembershipType.class).list();
        }
    }

    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(m) FROM MembershipType m", Long.class).uniqueResult();
        }
    }
}
