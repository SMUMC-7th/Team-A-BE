package com.example.echo.domain.capsule.dto.response;

import java.util.List;

public record ChatGPTResponseDTO(
        List<Question> questions
) {
    public record Question(String text) {}
}

/* 가정한 API 응답 형식
    {
        "questions": [
            {"text": "질문 1"},
            {"text": "질문 2"},
            {"text": "질문 3"},
            {"text": "질문 4"},
            {"text": "질문 5"}
        ]
    }
*/
