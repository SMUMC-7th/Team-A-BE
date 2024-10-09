package com.example.echo.domain.user.dto.request;

import lombok.Getter;

public class UserReqDto {

    public record CreateUserRequestDto(
        String email,
        String nickname,
        String password
    ) {
    }


    public record UpdateNicknameRequestDto(
            String newNickname,
            String password
    ){
    }


    public record UpdatePasswordRequestDto(
            String oldPassword,
            String newPassword
    ){
    }
}
