package com.example.echo.domain.capsule.service.command;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
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
import com.example.echo.domain.capsule.service.AwsS3Service;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CapsuleCommandService {

    private final CapsuleRepository capsuleRepository;
    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;

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
        // 이미지 리스트가 비어있는 경우, 받은 capsule 그대로 반환
        if (!imageIds.isEmpty()) {
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
        }
        return capsule;
    }

    public CapsuleResDTO.CapsuleDetailResDTO updateCapsule(Long capsuleId, CapsuleReqDTO.UpdateCapsuleReqDTO dto, AuthUser authUser) {

        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));
        if (!capsule.isOpened()) { throw new CapsuleException(CapsuleErrorCode.CLOSED_CAPSULE); }

        capsule.update(dto.content(), dto.tagName());
        return CapsuleConverter.toCapsuleDetailDto(capsule, authUser);
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

    @Scheduled(cron = "0 0 2 * * ?")
    public String hardDeleteCapsule() {
        // 1. Soft delete된 캡슐
        List<Capsule> deletedCapsules = capsuleRepository.findAllByDeletedAtIsNotNull();
        if (deletedCapsules.isEmpty()) {
            return "삭제할 soft delete 캡슐이 없습니다.";
        }

        for (Capsule capsule : deletedCapsules) {
            // 2. 연관된 이미지의 매핑 해제 및 삭제
            for (Image image : capsule.getImages()) {
                image.setCapsule(null); // 캡슐 매핑 해제
                awsS3Service.deleteImage(image.getId());
            }

            // 3. 캡슐 삭제
            capsuleRepository.delete(capsule); // 하드 삭제
        }

        return "soft delete된 capsule이 삭제되었습니다.";
    }
}
