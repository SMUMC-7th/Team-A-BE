package com.example.echo.domain.capsule.dto.request;

import java.util.List;

// API 에 요청을 보내기 위한 데이터 전송 객체
public record ChatGPTReqDTO(
        String model,
        List<ChatGPTMsgDTO> messages
) {
    public ChatGPTReqDTO(String model, List<ChatGPTMsgDTO> messages) {
        this.model = model;
        this.messages = messages;
    }
}