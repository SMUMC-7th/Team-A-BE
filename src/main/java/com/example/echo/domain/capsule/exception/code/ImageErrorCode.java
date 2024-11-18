package com.example.echo.domain.capsule.exception.code;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE404", "이미지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
