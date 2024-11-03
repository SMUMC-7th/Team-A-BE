package com.example.echo.domain.capsule.converter;

import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Image;
import com.example.echo.domain.capsule.entity.Tag;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CapsuleConverter {
    public static User convertToUser(AuthUser authUser) {
        return User.builder()
                .id(authUser.getId()) // 필요한 필드 매핑
                .build();
    }


    // DTO -> Entity
    public static Capsule toCapsule(CapsuleReqDTO.CreateCapsuleReqDTO reqDTO, Tag tag, AuthUser authUser) {
        return Capsule.builder()
                .title(reqDTO.title())
                .content(reqDTO.content())
                .deadLine(reqDTO.deadline())
                .isOpened(false)
                .tag(tag) // 태그 설정
                .user(convertToUser(authUser)) // 사용자 정보 설정
                .build();
    }

    //Entity -> DTO
    public static CapsuleResDTO.CapsuleResponseDTO toCreateCapsuleResponseDto(Capsule capsule) {
        return CapsuleResDTO.CapsuleResponseDTO.builder()
                .id(capsule.getId())
                .createdAt(capsule.getCreatedAt()) // 생성 시간
                .build();
    }

    //Entity리스트 -> DTO리스트
    public static List<CapsuleResDTO.CapsulePreviewResDTO> fromList(List<Capsule> capsules, AuthUser authUser) {
        return capsules.stream()
                .map(capsule -> toCapsulePreviewDto(capsule, authUser)) // 각 Capsule을 CapsulePreviewResDTO로 변환
                .collect(Collectors.toList());
    }

    // Entity -> Preview DTO
    public static CapsuleResDTO.CapsulePreviewResDTO toCapsulePreviewDto(Capsule capsule, AuthUser authUser) {
        return CapsuleResDTO.CapsulePreviewResDTO.builder()
                .id(capsule.getId())
                .userId(authUser.getId())
                .tagId(capsule.getTag().getId())
                .isOpened(capsule.isOpened())
                .title(capsule.getTitle())
                .createdAt(capsule.getCreatedAt())
                .now(LocalDateTime.now())
                .deadline(capsule.getDeadLine())
                .build();
    }

    // Entity -> Detail DTO
    public static CapsuleResDTO.CapsuleDetailResDTO toCapsuleDetailDto(Capsule capsule, AuthUser authUser) {
        List<String> imageUrls = capsule.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        return CapsuleResDTO.CapsuleDetailResDTO.builder()
                .capsuleId(capsule.getId())
                .userId(authUser.getId())
                .isOpened(capsule.isOpened())
                .title(capsule.getTitle())
                .content(capsule.getContent())
                .image(imageUrls)
                .tagId(capsule.getTag().getId())
                .createdAt(capsule.getCreatedAt())
                .now(LocalDateTime.now())
                .deadline(capsule.getDeadLine())
                .build();
    }
}
