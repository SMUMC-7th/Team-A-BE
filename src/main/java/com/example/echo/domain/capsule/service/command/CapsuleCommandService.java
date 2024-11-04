package com.example.echo.domain.capsule.service.command;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Image;
import com.example.echo.domain.capsule.entity.TagName;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleCommandService {
    private final CapsuleRepository capsuleRepository;

    public CapsuleResDTO.CapsuleResponseDTO createCapsule(
            CapsuleReqDTO.CreateCapsuleReqDTO reqDTO, AuthUser authUser) {
        if (authUser == null) {
            throw new UserException(UserErrorCode.NO_USER_DATA_REGISTERED);
        }

        Capsule newCapsule = CapsuleConverter.toCapsule(reqDTO, authUser);
        newCapsule = capsuleRepository.save(newCapsule);

        return CapsuleConverter.toCreateCapsuleResponseDto(newCapsule);
    }

    public void deleteCapsule(Long id) {
        Capsule capsule = capsuleRepository.findById(id).orElseThrow(() ->
                new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        capsule.softDelete();

        // 연관된 Image softdelete
        for (Image image : capsule.getImages()) {
            image.softDelete();
        }
    }

    public void restoreCapsule(Long id) {
        Capsule capsule = capsuleRepository.findById(id).orElseThrow(() ->
                new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        capsule.restore();

        // 연관된 Image restore
        for (Image image : capsule.getImages()) {
            image.restore();
        }
    }
}
