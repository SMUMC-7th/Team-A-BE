package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    // capsule 이 null 이고, 현재 기준으로 24시간 전보다 이전에 생성된 Image 반환
    List<Image> findByCapsuleIsNullAndCreatedAtIsBefore(LocalDateTime dateTime);
}
