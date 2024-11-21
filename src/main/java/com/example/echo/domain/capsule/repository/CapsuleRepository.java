package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.security.entity.AuthUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CapsuleRepository extends JpaRepository<Capsule,Long> {
    Optional<Capsule> findByIdAndDeletedAtIsNull(Long capsuleId);
    List<Capsule> findByUserIdAndDeletedAtIsNull(Long userId);
    Slice<Capsule> findByDeadLineAndIsOpenedFalse(LocalDate deadLine, Pageable pageable);
    Slice<Capsule> findByUserIdAndDeletedAtIsNullAndIdGreaterThanOrderByIdAsc(Long userId, Long cursor, Pageable pageable);

    Slice<Capsule> findByUserIdAndDeletedAtIsNullOrderByIdAsc(Long userId,Pageable pageable);
}
