package com.example.echo.domain.notification.dto.request;

import lombok.Builder;

public class NotificationReqDto {

    @Builder
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
