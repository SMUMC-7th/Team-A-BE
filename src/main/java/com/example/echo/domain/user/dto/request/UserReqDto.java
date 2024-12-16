package com.example.echo.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserReqDto {

    @Schema(description = "회원가입 DTO")
    public record CreateUserRequestDto(
        @Schema(description = "이메일")
        String email,
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "비밀번호")
        String password
    ) {
    }

    @Schema(description = "OAuth2 로그인&회원가입 DTO")
    public record OAuthUserRequestDto(
            @Schema(description = "이메일")
            String email,

            @Schema(description = "닉네임")
            String nickname
    ){
    }

    @Schema(description = "닉네임 수정 DTO")
    public record UpdateNicknameRequestDto(
            @Schema(description = "바꿀 닉네임")
            String newNickname
    ){
    }


    @Schema(description = "마이페이지 비밀번호 수정 DTO")
    public record UpdateAuthPasswordRequestDto(
            @Schema(description = "기존 비밀번호")
            String oldPassword,

            @Schema(description = "신규 비밀번호")
            String newPassword
    ){
    }

    @Schema(description = "로그인 전 비밀번호 찾기 DTO")
    public record UpdatePasswordRequestDto(
            @Schema(description = "계정 이메일")
            String email,

            @Schema(description = "초기화할 비밀번호")
            String password
    ){
    }
}
