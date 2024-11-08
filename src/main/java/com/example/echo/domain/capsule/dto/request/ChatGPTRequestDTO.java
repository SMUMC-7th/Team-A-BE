package com.example.echo.domain.capsule.dto.request;

public record ChatGPTRequestDTO(
        String model,
        String prompt,
        int max_tokens
) {
    public ChatGPTRequestDTO(String prompt) {
        this("gpt-3.5-turbo", prompt, 100); // 100젇도면 적절하려나...? 혹은 150정도...?
    }
}
