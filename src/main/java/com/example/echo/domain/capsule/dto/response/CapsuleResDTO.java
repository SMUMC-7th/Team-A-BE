package com.example.echo.domain.capsule.dto.response;

import com.example.echo.domain.capsule.entity.TagName;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CapsuleResDTO {
    @Builder
    public record CapsuleResponseDTO(
            Long id,
            LocalDateTime createdAt
           ){}

    @Builder
    public record CapsuleDetailResDTO(
            Long capsuleId,
            Long userId,
            boolean isOpened, // boolean 타입 유지
            String title,
            String content,
            List<String> image, // 이미지 URL 리스트
            TagName tagName,
            LocalDateTime createdAt,
            LocalDateTime now,
            LocalDate deadline

    ){}

    @Builder
    public record CapsulePreviewResDTO(
            Long id,
            Long userId,
            boolean isOpened,
            String title,
            TagName tagName,
            LocalDateTime createdAt,
            LocalDateTime now,
            LocalDate deadline
    ){}
}
