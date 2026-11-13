package com.ekabotdev.library.auth.dto;

public record RegisterResponse (
        Long userId,
        String name,
        String email,
        String role
) {
}
