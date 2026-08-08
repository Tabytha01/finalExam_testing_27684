package com.auca.library;

import com.auca.library.dao.MembershipTypeDao;
import com.auca.library.domain.MembershipType;

public class DataSeeder {

    private final MembershipTypeDao membershipTypeDao = new MembershipTypeDao();

    public void seedMembershipTypes() {
        if (membershipTypeDao.count() > 0) return;

        MembershipType gold = new MembershipType();
        gold.setMembershipName("GOLD");
        gold.setPrice(50);
        gold.setMaxBooks(5);
        membershipTypeDao.save(gold);

        MembershipType silver = new MembershipType();
        silver.setMembershipName("SILVER");
        silver.setPrice(30);
        silver.setMaxBooks(3);
        membershipTypeDao.save(silver);

        MembershipType striver = new MembershipType();
        striver.setMembershipName("STRIVER");
        striver.setPrice(10);
        striver.setMaxBooks(2);
        membershipTypeDao.save(striver);

        System.out.println("Membership types seeded.");
    }
}
