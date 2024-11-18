package com.example.echo.domain.user.dto.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserReqDto {

    public record CreateUserRequestDto(
        String email,
        String nickname,
        String password
    ) {
    }

    public record OAuthUserRequestDto(
            String email,
            String nickname
    ){
    }

    public record UpdateNicknameRequestDto(
            String newNickname
    ){
    }


    public record UpdateAuthPasswordRequestDto(
            String oldPassword,
            String newPassword
    ){
    }

    public record UpdatePasswordRequestDto(
            String email,
            String password
    ){
    }
}
