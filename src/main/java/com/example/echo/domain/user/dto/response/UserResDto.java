package com.example.echo.domain.user.dto.response;

import lombok.Builder;

public class UserResDto {
    @Builder
    public record UserResponseDto(
            Long id,
            String email,
            String nickname
    ){
    }
}
