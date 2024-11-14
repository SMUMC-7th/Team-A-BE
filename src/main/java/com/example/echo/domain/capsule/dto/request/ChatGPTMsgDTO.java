package com.example.echo.domain.capsule.dto.request;

// API 에서 사용되는 대화 메시지
public record ChatGPTMsgDTO(
        String role, // 발신자의 역할
        String content // 메시지 내용
) {}
