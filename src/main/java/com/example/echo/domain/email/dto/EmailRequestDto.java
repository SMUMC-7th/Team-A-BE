package com.example.echo.domain.email.dto;

public class EmailRequestDto {
    public record SendEmailRequestDto(
            String email
    ){}

    public record VerifyEmailRequestDto(
            String email,
            String code
    ){}
}
