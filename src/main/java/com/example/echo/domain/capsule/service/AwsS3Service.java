package com.example.echo.domain.capsule.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Image;
import com.example.echo.domain.capsule.exception.code.AwsS3ErrorCode;
import com.example.echo.domain.capsule.exception.handler.AwsS3Exception;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.capsule.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public Image uploadImage(MultipartFile multipartFile) {

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

        // 이미지 메타데이터 객체 생성
        return imageRepository.save(Image.builder()
                .imageUrl(s3FileName)
                .build());
    }

    // S3와 DB에 이미지 삭제
    public void deleteImage(Long capsuleId, Long imageId) {

        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new IllegalArgumentException("Capsule not found with id: " + capsuleId));

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));

        // S3 에서 이미지 삭제
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, image.getImageUrl());
        amazonS3.deleteObject(deleteObjectRequest);

        // DB 에서 이미지 삭제
        capsule.getImages().remove(image);
        imageRepository.delete(image);
    }

    // UUID 사용하여 새로운 파일 이름 생성
    private String createUniqueName(String fileName) {
        return UUID.randomUUID() + fileName;
    }
}
