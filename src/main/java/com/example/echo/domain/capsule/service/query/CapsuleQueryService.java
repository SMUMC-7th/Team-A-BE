package com.example.echo.domain.capsule.service.query;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.capsule.repository.TagRepository;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleQueryService {
    private final CapsuleRepository capsuleRepository;

    public List<CapsuleResDTO.CapsulePreviewResDTO> getCapsules(AuthUser authUser){
        List<Capsule> capsules = capsuleRepository.findByUserIdAndDeletedAtIsNull(authUser.getId());
        return CapsuleConverter.fromList(capsules, authUser);
    }

    public CapsuleResDTO.CapsuleDetailResDTO getCapsule(Long capsuleId, AuthUser authUser){
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        return CapsuleConverter.toCapsuleDetailDto(capsule, authUser);
    }
}
