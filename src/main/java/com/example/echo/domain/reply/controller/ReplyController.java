package com.example.echo.domain.reply.controller;


import com.example.echo.domain.reply.dto.request.ReplyRequestDto;
import com.example.echo.domain.reply.dto.response.ReplyResponseDto;
import com.example.echo.domain.reply.service.command.ReplyCommandService;
import com.example.echo.domain.reply.service.query.ReplyQueryService;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
@Tag(name = "댓글")
public class ReplyController {

    private final ReplyCommandService replyCommandService;
    private final ReplyQueryService replyQueryService;

    @PostMapping
    public ResponseEntity<ReplyResponseDto.ReplyPreviewDto> addReply(@RequestBody ReplyRequestDto.CreateReplyReqDto requestDto) {
        return ResponseEntity.ok(replyCommandService.addReply(requestDto));
    }

    @GetMapping("/{capsuleId}")
    public ResponseEntity<List<ReplyResponseDto.ReplyPreviewDto>> getReplies(@PathVariable Long capsuleId) {
        return ResponseEntity.ok(replyQueryService.getReplies(capsuleId));
    }
}
