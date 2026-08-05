package com.auca.library.domain;

import com.auca.library.domain.enums.MembershipStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "membership_id")
    private UUID membershipId;

    @Column(unique = true)
    private String membershipCode;

    @Column(name = "reader_id")
    private UUID readerId;

    @Column(name = "membership_type_id")
    private UUID membershipTypeId;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    private LocalDate registrationDate;
    private LocalDate expiringTime;

    public UUID getMembershipId() { return membershipId; }
    public void setMembershipId(UUID membershipId) { this.membershipId = membershipId; }
    public String getMembershipCode() { return membershipCode; }
    public void setMembershipCode(String membershipCode) { this.membershipCode = membershipCode; }
    public UUID getReaderId() { return readerId; }
    public void setReaderId(UUID readerId) { this.readerId = readerId; }
    public UUID getMembershipTypeId() { return membershipTypeId; }
    public void setMembershipTypeId(UUID membershipTypeId) { this.membershipTypeId = membershipTypeId; }
    public MembershipStatus getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(MembershipStatus membershipStatus) { this.membershipStatus = membershipStatus; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public LocalDate getExpiringTime() { return expiringTime; }
    public void setExpiringTime(LocalDate expiringTime) { this.expiringTime = expiringTime; }
}
