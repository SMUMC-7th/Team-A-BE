package com.example.echo.domain.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private final Long id;

    private final String email;

    @JsonIgnore
    private final String password;
}