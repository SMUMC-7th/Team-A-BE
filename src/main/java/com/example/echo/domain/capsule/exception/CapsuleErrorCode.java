package com.example.echo.domain.capsule.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CapsuleErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND,
              "Capsule404",
            "게시글을 찾을 수 없습니다."),
    INVALID_TAG_NAME(HttpStatus.BAD_REQUEST,
            "Capsule400", "유효하지 않은 태그 이름입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
