package com.example.echo.domain.user.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {


    NO_USER_DATA_REGISTERED(HttpStatus.NOT_FOUND,
            "USER404_1", "사용자 데이터 값이 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,
            "USER400_1", "비밀번호가 다릅니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
