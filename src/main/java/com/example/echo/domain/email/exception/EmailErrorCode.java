package com.example.echo.domain.email.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements BaseErrorCode {

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,
            "EMAIL400_1", "인증 실패");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
