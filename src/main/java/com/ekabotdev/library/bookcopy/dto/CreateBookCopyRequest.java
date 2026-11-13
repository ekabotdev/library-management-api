package com.ekabotdev.library.bookcopy.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBookCopyRequest(
        @NotNull
        Long bookId
) {
}
