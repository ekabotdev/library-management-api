package com.ekabotdev.library.book.dto;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        String description,
        Integer publicationYear,
        Long authorId

) {
}
