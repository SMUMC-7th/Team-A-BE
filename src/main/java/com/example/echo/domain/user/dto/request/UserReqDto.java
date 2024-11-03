package com.example.echo.domain.user.dto.request;

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


    public record UpdatePasswordRequestDto(
            String oldPassword,
            String newPassword
    ){
    }
}