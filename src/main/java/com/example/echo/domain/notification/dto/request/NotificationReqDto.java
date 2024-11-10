package com.example.echo.domain.notification.dto.request;

public class NotificationReqDto {

    public record FcmSendDto(
            String fcmToken,
            String title,
            String body,
            Long userId,
            Long capsuleId
    ) {
    }

    public record FcmTokenRequestDto(
            String fcmToken
    ) {
    }
}
