package com.example.echo.domain.capsule.exception.handler;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import com.example.echo.global.apiPayload.exception.CustomException;

public class ChatGPTException extends CustomException {
    public ChatGPTException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
