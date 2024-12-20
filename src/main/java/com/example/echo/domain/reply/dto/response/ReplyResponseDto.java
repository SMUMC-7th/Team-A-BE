package com.example.echo.domain.reply.dto.response;

import jakarta.persistence.Column;
import lombok.Builder;

import java.time.LocalDateTime;

public class ReplyResponseDto {

    @Builder
    public record ReplyPreviewDto(
            Long id,
            Long timeCapsuleId,
            Long userId,
            Long ref,
            Long refOrder,
            Long step,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
