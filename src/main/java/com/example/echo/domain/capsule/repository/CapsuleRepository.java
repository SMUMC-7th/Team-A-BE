package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapsuleRepository extends JpaRepository<Capsule,Long> {
    //List<Capsule> findByUserId(Long userId);
    List<Capsule> findByUserIdAndDeletedAtIsNull(Long userId);
}
