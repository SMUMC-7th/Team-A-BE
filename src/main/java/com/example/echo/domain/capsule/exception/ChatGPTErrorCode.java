package com.example.echo.domain.capsule.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ChatGPTErrorCode implements BaseErrorCode {

    FAILED_TO_CALL_API(HttpStatus.INTERNAL_SERVER_ERROR, "API500", "API 호출 중 오류가 발생했습니다."),
    NULL_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "API500", "API 응답이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
