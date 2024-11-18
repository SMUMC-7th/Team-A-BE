package com.example.echo.domain.capsule.exception.handler;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import com.example.echo.global.apiPayload.exception.CustomException;

public class AwsS3Exception extends CustomException {
    public AwsS3Exception(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
