package com.ekabotdev.library.loan.dto;

import jakarta.validation.constraints.NotNull;

public record CreateLoanRequest(
        @NotNull
        Long bookCopyId
) {
}
