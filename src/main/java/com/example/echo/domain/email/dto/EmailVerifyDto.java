package com.example.echo.domain.email.dto;

public record EmailVerifyDto(
        String email,
        String code
) {
}
