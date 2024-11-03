package com.example.echo.domain.security.dto;

import lombok.Builder;


@Builder
public record JwtDto (
        String accessToken,

        String refreshToken
){

}