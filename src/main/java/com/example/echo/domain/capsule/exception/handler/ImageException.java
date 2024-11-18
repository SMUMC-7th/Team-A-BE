package com.example.echo.domain.capsule.exception.handler;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import com.example.echo.global.apiPayload.exception.CustomException;

public class ImageException extends CustomException {
    public ImageException(BaseErrorCode errorCode){super(errorCode);}
}
