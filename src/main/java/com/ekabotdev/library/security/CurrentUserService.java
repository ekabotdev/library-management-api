package com.ekabotdev.library.security;

import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final MemberRepository memberRepository;

    public Member getCurrentMember(Authentication authentication) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getUser().getId();

        return memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user is not a member"
                        ));
    }
}