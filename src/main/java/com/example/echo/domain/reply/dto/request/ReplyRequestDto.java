package com.example.echo.domain.reply.dto.request;

public class ReplyRequestDto {

    public record CreateReplyReqDto(
        Long timeCapsuleId,
        Long userId,
        String content,
        Long parentId
    ){}
}
