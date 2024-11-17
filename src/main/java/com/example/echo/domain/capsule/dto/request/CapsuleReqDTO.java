package com.example.echo.domain.capsule.dto.request;

import java.time.LocalDate;
import java.util.List;

public class CapsuleReqDTO {
    public record CreateCapsuleReqDTO(
            String title,
            String content,
            LocalDate deadline,
            String tagName,
            List<Long> imageList
    ){}
}