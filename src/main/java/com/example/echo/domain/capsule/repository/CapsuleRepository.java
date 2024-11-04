package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CapsuleRepository extends JpaRepository<Capsule,Long> {
    Optional<Capsule> findByIdAndDeletedAtIsNull(Long capsuleId);
    List<Capsule> findByUserIdAndDeletedAtIsNull(Long userId);
}
