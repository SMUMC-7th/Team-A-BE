package com.example.echo.domain.notification.repository;

import com.example.echo.domain.notification.entity.Noti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Noti, Long> {

}
