package com.example.echo.domain.reply.dto.request;

public class ReplyRequestDto {

    public record CreateReplyReqDto(
        Long capsuleId,
        Long userId,
        String content,
        Long parentId
    ){}
}
