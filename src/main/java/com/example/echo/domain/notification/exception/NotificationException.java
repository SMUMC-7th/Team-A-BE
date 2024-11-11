package com.example.echo.domain.notification.exception;

import com.example.echo.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class NotificationException extends CustomException {

    public NotificationException(NotificationErrorCode errorCode){
        super(errorCode);
    }
}
