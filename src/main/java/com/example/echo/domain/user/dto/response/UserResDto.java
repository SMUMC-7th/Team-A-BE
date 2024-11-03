package com.example.echo.domain.user.dto.response;

import com.example.echo.domain.user.entity.AuthType;
import lombok.Builder;

public class UserResDto {
    @Builder
    public record UserResponseDto(
            Long id,
            String email,
            String nickname,
            AuthType authType
    ){
    }

}
