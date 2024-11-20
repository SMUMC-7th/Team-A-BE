package com.example.echo.domain.capsule.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Image;
import com.example.echo.domain.capsule.exception.code.AwsS3ErrorCode;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.code.ImageErrorCode;
import com.example.echo.domain.capsule.exception.handler.AwsS3Exception;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
import com.example.echo.domain.capsule.exception.handler.ImageException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.capsule.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final CapsuleRepository capsuleRepository;
    private final ImageRepository imageRepository;

    // S3와 DB에 이미지 생성
    public List<Image> uploadImages(List<MultipartFile> multipartFiles) {

        // S3 업로드 완료한 이미지들의 메타데이터를 담을 리스트
        List<Image> images = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new AwsS3Exception(AwsS3ErrorCode.INVALID_FILE_UPLOAD);
            }
            // 고유한 UUID 사용하여 새로운 파일 이름 생성 (동일 파일명일 경우 대비)
            String s3FileName = createUniqueName(multipartFile.getOriginalFilename());

            try {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                objectMetadata.setContentLength(multipartFile.getSize());

                amazonS3.putObject(new PutObjectRequest(bucket, s3FileName, multipartFile.getInputStream(), objectMetadata));

            } catch (IOException e) {
                throw new AwsS3Exception(AwsS3ErrorCode.FILE_UPLOAD_FAILED);
            }

            Image image = imageRepository.save(Image.builder()
                    .imageUrl(s3FileName)
                    .build());

            images.add(image);
        }
        // 전체 업로드 된 이미지 리스트 반환
        return images;
    }

    // S3와 DB에 이미지 삭제
    public void deleteImage(Long capsuleId, Long imageId) {

        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(ImageErrorCode.NOT_FOUND));

        // S3 에서 이미지 삭제
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, image.getImageUrl());
        amazonS3.deleteObject(deleteObjectRequest);

        // DB 에서 이미지 삭제
        capsule.getImages().remove(image);
        imageRepository.delete(image);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    // 생성된지 24시간 이후에도 캡슐과 매핑되지 않은 이미지 삭제 (새벽 2시마다 실행)
    public String deleteOrphanedImages() {

        List<Image> images = imageRepository.findByCapsuleIsNullAndCreatedAtIsBefore(LocalDateTime.now().minusDays(1));
        if (images.isEmpty()) {
            return "모든 이미지가 캡슐에 속해있습니다. (삭제된 이미지 없음)"; }

        for (Image image : images) {
            // S3 에서 이미지 삭제
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, image.getImageUrl());
            amazonS3.deleteObject(deleteObjectRequest);
        }

        // DB 에서 전체 이미지 삭제
        imageRepository.deleteAll(images);
        return "캡슐이 할당되지 않은 이미지 객체를 성공적으로 삭제하였습니다.";
    }

    // UUID 사용하여 새로운 파일 이름 생성
    private String createUniqueName(String fileName) {
        return UUID.randomUUID() + fileName;
    }
}
