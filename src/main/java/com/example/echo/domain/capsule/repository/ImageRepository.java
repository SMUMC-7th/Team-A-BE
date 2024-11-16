package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
