package com.example.echo.domain.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Schema(description = "로그인 DTO")
public record LoginRequestDto (

    @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
    @Schema(description = "이메일")
    String email,

    @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
    @Schema(description = "비밀번호")
    String password
){}
