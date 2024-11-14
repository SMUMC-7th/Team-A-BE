package com.example.echo.domain.capsule.dto.request;

import com.example.echo.domain.capsule.entity.TagName;

import java.time.LocalDate;

public class CapsuleReqDTO {
    public record CreateCapsuleReqDTO(
            String title,
            String content,
            LocalDate deadline,
            String tagName
    ){}
}