package com.example.echo.domain.reply.dto.response;

import jakarta.persistence.Column;

public class ReplyResponseDto {
    public record ReplyPreviewDto(
    Long id,
    Long ref,

    Long ref_step,
    Long ref_level,
    String content

    ){}
}
