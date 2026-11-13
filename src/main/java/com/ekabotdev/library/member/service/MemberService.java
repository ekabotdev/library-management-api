package com.ekabotdev.library.member.service;

import com.ekabotdev.library.member.dto.MemberResponse;
import com.ekabotdev.library.member.dto.UpdateStatusRequest;
import com.ekabotdev.library.member.entity.Member;
import com.ekabotdev.library.member.exception.MemberNotFoundException;
import com.ekabotdev.library.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(this::toMemberResponse);
    }

    private MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getMembershipStatus(),
                member.getPhone()
        );
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() ->
        new MemberNotFoundException("Member with id: " + id + " not found"));

        return toMemberResponse(member);
    }

    @Transactional
    public MemberResponse updateMember(Long id,UpdateStatusRequest request) {
        Member member = memberRepository.findById(id).orElseThrow(()
                -> new MemberNotFoundException("Member with id: " + id + " not found"));

   if (!request.status().equals(member.getMembershipStatus())) {
       member.setMembershipStatus(request.status());
   }
        return toMemberResponse(member);

    }
}
