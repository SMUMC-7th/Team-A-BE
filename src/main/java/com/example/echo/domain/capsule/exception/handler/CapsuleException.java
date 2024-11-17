package com.example.echo.domain.capsule.exception.handler;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import com.example.echo.global.apiPayload.exception.CustomException;

public class CapsuleException extends CustomException {
    public CapsuleException(BaseErrorCode errorCode){super(errorCode);}
}
