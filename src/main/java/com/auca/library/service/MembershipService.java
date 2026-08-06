package com.auca.library.service;

import com.auca.library.dao.MembershipDao;
import com.auca.library.domain.Membership;
import com.auca.library.domain.enums.MembershipStatus;
import com.auca.library.exception.ActiveMembershipExistsException;

import java.time.LocalDate;
import java.util.UUID;

public class MembershipService {

    private final MembershipDao membershipDao = new MembershipDao();

    public Membership registerMembership(UUID userId, UUID membershipTypeId) {
        if (!membershipDao.findActiveByReaderId(userId).isEmpty()) {
            throw new ActiveMembershipExistsException("User already has an active or pending membership");
        }
        Membership membership = new Membership();
        membership.setMembershipCode(UUID.randomUUID().toString());
        membership.setReaderId(userId);
        membership.setMembershipTypeId(membershipTypeId);
        membership.setMembershipStatus(MembershipStatus.PENDING);
        membership.setRegistrationDate(LocalDate.now());
        membershipDao.save(membership);
        return membership;
    }
}
