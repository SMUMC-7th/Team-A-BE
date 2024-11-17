package com.example.echo.domain.capsule.service.command;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.dto.response.CapsuleResDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Image;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.code.ImageErrorCode;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
import com.example.echo.domain.capsule.exception.handler.ImageException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.capsule.repository.ImageRepository;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleCommandService {

    private final CapsuleRepository capsuleRepository;
    private final ImageRepository imageRepository;

    // 캡슐 생성 및 이미지 매핑(로직 호출)
    public CapsuleResDTO.CapsuleResponseDTO createCapsule(CapsuleReqDTO.CreateCapsuleReqDTO reqDTO, AuthUser authUser) {

        Capsule capsule = mapCapsuleImage(createPartialCapsule(reqDTO, authUser), reqDTO.imageList());
        return CapsuleConverter.toCreateCapsuleResponseDto(capsule);
    }

    // 이미지 리스트 미포함 캡슐 객체 생성
    public Capsule createPartialCapsule(
            CapsuleReqDTO.CreateCapsuleReqDTO reqDTO, AuthUser authUser) {
        if (authUser == null) {
            throw new UserException(UserErrorCode.NO_USER_DATA_REGISTERED);
        }

        Capsule newCapsule = CapsuleConverter.toCapsule(reqDTO, authUser);
        newCapsule = capsuleRepository.save(newCapsule);

        return newCapsule;
    }

    // 캡슐 객체와 이미지 매핑
    public Capsule mapCapsuleImage(Capsule capsule, List<Long> imageIds) {

        // 각 이미지 객체에 속하는 캡슐 set
        List<Image> images = imageIds.stream()
                .map(imageId -> {
                    Image image = imageRepository.findById(imageId)
                            .orElseThrow(() -> new ImageException(ImageErrorCode.NOT_FOUND));
                    image.setCapsule(capsule);
                    return image;
                })
                .toList();

        // 속하는 캡슐에 이미지 리스트 set
        capsule.setImageList(images);
        return capsule;
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
