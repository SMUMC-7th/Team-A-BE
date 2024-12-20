package com.example.echo.domain.capsule.exception.code;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CapsuleErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND,
              "Capsule404",
            "타임캡슐을 찾을 수 없습니다."),
    INVALID_TAG_NAME(HttpStatus.BAD_REQUEST,
            "Capsule400",
            "유효하지 않은 태그 이름입니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND,
            "CapsuleTag404",
            "존재하지 않은 태그 이름입니다."),
    CLOSED_CAPSULE(HttpStatus.FORBIDDEN,
            "CapsuleTag409",
            "열리지 않은 타임캡슐입니다."),
    CAPSULE_NOT_FOUND(HttpStatus.NOT_FOUND,
            "Capsule404_1",
            "저장된 타임캡슐을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
