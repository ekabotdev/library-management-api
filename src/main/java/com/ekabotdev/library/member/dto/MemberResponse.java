package com.ekabotdev.library.member.dto;

import com.ekabotdev.library.enums.MembershipStatus;
import com.ekabotdev.library.user.entity.User;

public record MemberResponse(
        Long id,
        Long userId,
        MembershipStatus status,
        String phone
) {
}
