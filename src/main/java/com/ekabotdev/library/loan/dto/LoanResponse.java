package com.ekabotdev.library.loan.dto;

import com.ekabotdev.library.enums.LoanStatus;

import java.time.LocalDateTime;

public record LoanResponse(
        Long id,
        Long memberId,
        Long bookCopyId,
        LocalDateTime borrowedAt,
        LocalDateTime dueDate,
        LocalDateTime returnedAt,
        LoanStatus status
) {
}
