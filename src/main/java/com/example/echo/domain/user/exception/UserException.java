package com.example.echo.domain.user.exception;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import com.example.echo.global.apiPayload.exception.CustomException;

public class UserException extends CustomException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
