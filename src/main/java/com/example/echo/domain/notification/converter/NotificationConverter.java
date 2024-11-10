package com.example.echo.domain.notification.converter;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.notification.dto.NotificationDto;
import com.example.echo.domain.notification.entity.Noti;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.repository.UserReposiotry;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationConverter {

    private final UserReposiotry userReposiotry;
    private final CapsuleRepository capsuleRepository;

    // FcmSendDto를 기반으로 Firebase Message 객체를 생성하는 메서드
    public static Message toFirebaseMessage(NotificationDto.FcmSendDto fcmSendDto) {
        // 알림 정보 설정
        Notification notification = Notification.builder()
                .setTitle(fcmSendDto.title())
                .setBody(fcmSendDto.body())
                .build();

        // 메시지 빌드 (토큰과 알림 내용 포함)
        return Message.builder()
                .setToken(fcmSendDto.fcmToken())
                .setNotification(notification)
                .build();
    }

    public Noti toNoti(NotificationDto.FcmSendDto fcmSendDto, User user, Capsule capsule, boolean success) {
        return Noti.builder()
                .title(fcmSendDto.title())
                .body(fcmSendDto.body())
                .user(user)
                .capsule(capsule)
                .success(success)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
