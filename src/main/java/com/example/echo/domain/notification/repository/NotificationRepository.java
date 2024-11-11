package com.example.echo.domain.notification.repository;

import com.example.echo.domain.notification.entity.Noti;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Noti, Long> {

    @Query("SELECT n FROM Noti n WHERE (:cursor IS NULL OR n.createdAt < (SELECT createdAt FROM Noti WHERE id = :cursor)) ORDER BY n.createdAt DESC")
    Slice<Noti> findNotificationsByCreatedAtCursor(@Param("cursor") Long cursor, Pageable pageable);
}
