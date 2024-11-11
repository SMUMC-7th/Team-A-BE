package com.example.echo.domain.notification.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationResDto {

    @Builder
    public record NotificationPreviewDto(
            Long notificationId,
            Long capsuleId,
            String title,
            String body,
            boolean isRead,
            LocalDateTime createdAt
    ){
    }

    @Builder
    public record NotificationPreviewListDto(
            List<NotificationPreviewDto> notificationPreviewDtoList,
            boolean hasNext,
            Long cursor
    ){
    }
}
