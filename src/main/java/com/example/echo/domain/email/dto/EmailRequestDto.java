package com.example.echo.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public class EmailRequestDto {
    @Schema(description = "이메일 송신 DTO")
    public record SendEmailRequestDto(
            @Schema(description = "이메일")
            String email
    ){}

    @Schema(description = "인증코드 검증 DTO")
    public record VerifyEmailRequestDto(
            @Schema(description = "인증코드 보낼 이메일")
            String email,
            @Schema(description = "인증코드")
            String code
    ){}
}
