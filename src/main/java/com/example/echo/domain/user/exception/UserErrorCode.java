
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
    PASSWORD_UNCHANGED(HttpStatus.BAD_REQUEST, "USER400_2", "비밀번호가 기존이랑 같습니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN,
            "USER403_1", "계정이 비활성화 상태입니다."),
    WRONG_AUTH_TYPE(HttpStatus.BAD_REQUEST,
            "USER400_3", "잘못된 인증 방식입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST,
            "USER400_4", "이메일이 다릅니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER409_1", "이미 존재하는 이메일입니다."),
    DELETED_USER(HttpStatus.BAD_REQUEST, "USER400_5", "삭제 처리된 유저입니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "USER400_6", "인증코드가 일치하지 않습니다.")

    ;





    private final HttpStatus status;
    private final String code;
    private final String message;
}