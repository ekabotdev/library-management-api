package com.ekabotdev.library.author.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAuthorRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        String biography
) {
}
