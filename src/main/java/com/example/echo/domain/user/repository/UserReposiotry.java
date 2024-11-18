package com.example.echo.domain.user.repository;

import com.example.echo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserReposiotry extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.active = false AND u.deletedAt <= :expiryDate")
    void findByDeletedAtBefore(LocalDateTime expiryDate);
}
