package com.ekabotdev.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateBookRequest(

    @NotBlank
    @Size(min = 3, max = 100)
     String title,

    @NotBlank
    @Size(max = 100)
   String isbn,

     String description,

    @NotNull
     Integer publicationYear,

    @NotNull
    Long authorId

    )
{

}
