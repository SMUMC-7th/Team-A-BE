package com.example.echo.domain.reply.converter;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.reply.dto.request.ReplyRequestDto;
import com.example.echo.domain.reply.dto.response.ReplyResponseDto;
import com.example.echo.domain.reply.entity.Reply;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.repository.UserReposiotry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReplyConverter {

    private final UserReposiotry userRepository;
    private final CapsuleRepository capsuleRepository;

    public Reply toEntity(ReplyRequestDto.CreateReplyReqDto dto, Reply parent, Long ref, Long refOrder, Long step) {
        User user = findUserById(dto.userId());
        Capsule capsule = findCapsuleById(dto.capsuleId());

        return Reply.builder()
                .content(dto.content())
                .parent(parent) // 부모 댓글
                .ref(ref) // 댓글 그룹
                .refOrder(refOrder) // 출력 순서
                .step(step) // 댓글 깊이
                .capsule(capsule)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public ReplyResponseDto.ReplyPreviewDto toPreviewDto(Reply reply) {
        return ReplyResponseDto.ReplyPreviewDto.builder()
                .id(reply.getId())
                .timeCapsuleId(reply.getCapsule().getId())
                .userId(reply.getUser().getId())
                .content(reply.getContent())
                .ref(reply.getRef())
                .refOrder(reply.getRefOrder())
                .step(reply.getStep())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. (ID: " + userId + ")"));
    }

    private Capsule findCapsuleById(Long capsuleId) {
        return capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new IllegalArgumentException("캡슐을 찾을 수 없습니다. (ID: " + capsuleId + ")"));
    }
}
