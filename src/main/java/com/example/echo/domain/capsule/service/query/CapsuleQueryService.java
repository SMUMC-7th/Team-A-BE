package com.example.echo.domain.capsule.service.query;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.security.entity.AuthUser;
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
        capsules.forEach(capsule -> capsule.setIsOpened());
        capsuleRepository.saveAll(capsules);
        return CapsuleConverter.fromList(capsules, authUser);
    }

    public CapsuleResDTO.CapsuleDetailResDTO getCapsule(Long capsuleId, AuthUser authUser){
        Capsule capsule = capsuleRepository.findByIdAndDeletedAtIsNull(capsuleId)
                .orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        return CapsuleConverter.toCapsuleDetailDto(capsule, authUser);
    }
}
