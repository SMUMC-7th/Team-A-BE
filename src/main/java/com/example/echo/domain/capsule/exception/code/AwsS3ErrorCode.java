package com.example.echo.domain.capsule.exception.code;

import com.example.echo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AwsS3ErrorCode implements BaseErrorCode {

    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AWS500", "파일 업로드에 실패했습니다"),
    INVALID_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "AWS400", "파일이 비어있거나 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
