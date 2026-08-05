package com.auca.library.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "membership_types")
public class MembershipType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "membership_type_id")
    private UUID membershipTypeId;

    private String membershipName;
    private int price;
    private int maxBooks;

    public UUID getMembershipTypeId() { return membershipTypeId; }
    public void setMembershipTypeId(UUID membershipTypeId) { this.membershipTypeId = membershipTypeId; }
    public String getMembershipName() { return membershipName; }
    public void setMembershipName(String membershipName) { this.membershipName = membershipName; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getMaxBooks() { return maxBooks; }
    public void setMaxBooks(int maxBooks) { this.maxBooks = maxBooks; }
}
