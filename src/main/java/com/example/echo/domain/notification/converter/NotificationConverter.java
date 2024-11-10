package com.example.echo.domain.notification.converter;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.notification.dto.request.NotificationReqDto;
import com.example.echo.domain.notification.dto.response.NotificationResDto;
import com.example.echo.domain.notification.entity.Noti;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.repository.UserReposiotry;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationConverter {

    private final UserReposiotry userReposiotry;
    private final CapsuleRepository capsuleRepository;

    // FcmSendDto를 기반으로 Firebase Message 객체를 생성하는 메서드
    public static Message toFirebaseMessage(NotificationReqDto.FcmSendDto fcmSendDto) {
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

    public Noti toNoti(NotificationReqDto.FcmSendDto fcmSendDto, User user, Capsule capsule, boolean success) {
        return Noti.builder()
                .title(fcmSendDto.title())
                .body(fcmSendDto.body())
                .user(user)
                .capsule(capsule)
                .success(success)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static NotificationResDto.NotificationPreviewDto toNotificationDetail(Noti noti) {
        return NotificationResDto.NotificationPreviewDto.builder()
                .notificationId(noti.getId())
                .capsuleId(noti.getCapsule().getId())
                .title(noti.getTitle())
                .body(noti.getBody())
                .isRead(noti.isRead())
                .createdAt(noti.getCreatedAt())
                .build();
    }

    public static NotificationResDto.NotificationPreviewListDto toNotificationResponse(Slice<Noti> notifications, Long nextCursor) {
        List<NotificationResDto.NotificationPreviewDto> notificationDetails = notifications.stream()
                .map(NotificationConverter::toNotificationDetail)
                .toList();

        return NotificationResDto.NotificationPreviewListDto.builder()
                .notificationPreviewDtoList(notificationDetails)
                .hasNext(notifications.hasNext())
                .cursor(nextCursor)
                .build();
    }
}
