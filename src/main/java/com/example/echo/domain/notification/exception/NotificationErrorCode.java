package com.example.echo.domain.notification.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    FCMTOKEN_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Notification400_0", "해당 유저의 FCM 토큰이 이미 존재합니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification404_0", "해당 알림을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
