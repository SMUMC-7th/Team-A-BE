package com.example.echo.domain.user.repository;

import com.example.echo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReposiotry extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByEmail(String email);
}
