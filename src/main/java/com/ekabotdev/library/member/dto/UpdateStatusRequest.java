package com.ekabotdev.library.member.dto;

import com.ekabotdev.library.enums.MembershipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull
        MembershipStatus status
) {
}
