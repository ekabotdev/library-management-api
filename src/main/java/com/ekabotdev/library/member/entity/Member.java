package com.ekabotdev.library.member.entity;

import com.ekabotdev.library.common.BaseEntity;
import com.ekabotdev.library.enums.MembershipStatus;
import com.ekabotdev.library.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@RequiredArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "membership_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    @Column(nullable = false, length = 100)
    private String phone;

}
