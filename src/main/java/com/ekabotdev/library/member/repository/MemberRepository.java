package com.ekabotdev.library.member.repository;

import com.ekabotdev.library.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository  extends JpaRepository<Member,Long> {
    Optional<Member> findByUserId(Long id);
}
