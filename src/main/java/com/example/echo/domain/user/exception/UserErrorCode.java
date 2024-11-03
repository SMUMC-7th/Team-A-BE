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
            "USER400_1", "비밀번호가 다릅니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN,
            "USER403_1", "계정이 비활성화 상태입니다."),
    WRONG_AUTH_TYPE(HttpStatus.BAD_REQUEST,
            "USER400_2", "잘못된 인증 방식입니다.");




    private final HttpStatus status;
    private final String code;
    private final String message;
}