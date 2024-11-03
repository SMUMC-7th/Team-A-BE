package com.example.echo.domain.capsule.repository;

import com.example.echo.domain.capsule.entity.Tag;
import com.example.echo.domain.capsule.entity.TagName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
}
