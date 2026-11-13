package com.ekabotdev.library.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
