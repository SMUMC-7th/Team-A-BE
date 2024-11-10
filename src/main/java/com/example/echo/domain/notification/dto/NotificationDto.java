package com.example.echo.domain.notification.dto;

public class NotificationDto {

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
