package com.ekabotdev.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBookRequest(
        @NotBlank @Size(min = 3,max = 100)
        String title,
        String description
) {
}
