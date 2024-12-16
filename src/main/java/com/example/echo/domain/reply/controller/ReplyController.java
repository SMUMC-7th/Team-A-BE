package com.example.echo.domain.reply.controller;


import com.example.echo.domain.reply.dto.request.ReplyRequestDto;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
@Tag(name = "댓글 API")
public class ReplyController {

    public CustomResponse<String> createReply(@RequestBody ReplyRequestDto.CreateReplyReqDto createReplyReqDto){


    }

}
