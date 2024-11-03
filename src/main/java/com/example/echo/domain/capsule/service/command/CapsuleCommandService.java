package com.example.echo.domain.capsule.service.command;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.converter.TagConverter;
import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Tag;
import com.example.echo.domain.capsule.entity.TagName;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.capsule.repository.TagRepository;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.repository.UserReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleCommandService {
    private final CapsuleRepository capsuleRepository;
    private final TagRepository tagRepository; // TagRepository 추가

    public CapsuleResDTO.CapsuleResponseDTO createCapsule(
            CapsuleReqDTO.CreateCapsuleReqDTO reqDTO, AuthUser authUser) {
        if (authUser == null) {
            throw new IllegalArgumentException("사용자 정보가 필요합니다.");
        }

        // tagName을 TagName enum으로 변환
        TagName tagNameEnum;
        try {
            tagNameEnum = TagName.valueOf(reqDTO.tagName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CapsuleException(CapsuleErrorCode.INVALID_TAG_NAME); // 잘못된 태그 이름 처리
        }

        //데이터베이스에 저장
        Tag tag = tagRepository.save(TagConverter.toTag(tagNameEnum));

        Capsule newCapsule = CapsuleConverter.toCapsule(reqDTO, tag, authUser);
        newCapsule = capsuleRepository.save(newCapsule);

        return CapsuleConverter.toCreateCapsuleResponseDto(newCapsule);
    }

    public void deleteCapsule(Long id) {
        Capsule capsule = capsuleRepository.findById(id).orElseThrow(() ->
                new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        capsuleRepository.delete(capsule);
    }
}