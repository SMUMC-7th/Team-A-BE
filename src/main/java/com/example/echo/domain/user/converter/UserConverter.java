package com.example.echo.domain.user.converter;

import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserConverter {

    public static User toEntity(UserReqDto.CreateUserRequestDto dto, PasswordEncoder passwordEncoder){
        String encodePassword = passwordEncoder.encode(dto.password());
        return User.builder()
                .email(dto.email())
                .nickname(dto.nickname())
                .password(encodePassword)
                .active(true)
                .build();
    }


    public static UserResDto.UserResponseDto from(User user){
        return UserResDto.UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
