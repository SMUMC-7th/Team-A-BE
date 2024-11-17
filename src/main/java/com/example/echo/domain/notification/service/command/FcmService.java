package com.example.echo.domain.notification.service.command;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.CapsuleException;
import com.example.echo.domain.capsule.repository.CapsuleRepository;
import com.example.echo.domain.notification.converter.NotificationConverter;
import com.example.echo.domain.notification.dto.request.NotificationReqDto;
import com.example.echo.domain.notification.entity.Noti;
import com.example.echo.domain.notification.exception.NotificationErrorCode;
import com.example.echo.domain.notification.exception.NotificationException;
import com.example.echo.domain.notification.repository.NotificationRepository;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.repository.UserReposiotry;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FcmService {

    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;
    private final UserReposiotry userRepository;
    private final CapsuleRepository capsuleRepository;

    // FCM 푸시 알림 전송 메서드
    public void sendFcmNotification(NotificationReqDto.FcmSendDto fcmSendDto) {
        Message message = NotificationConverter.toFirebaseMessage(fcmSendDto);

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("푸시 알림 전송 성공: {}", response);

            // 알림 전송 성공 시 DB에 저장
            saveNotification(fcmSendDto, true);

        } catch (Exception e) {
            log.error("푸시 알림 전송 실패", e);

            // 알림 전송 실패 시 DB에 저장
            saveNotification(fcmSendDto, false);
        }
    }

    // 알림을 DB에 저장하는 메서드
    public void saveNotification(NotificationReqDto.FcmSendDto fcmSendDto, boolean success) {
        User user = userRepository.findById(fcmSendDto.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        Capsule capsule = capsuleRepository.findById(fcmSendDto.capsuleId())
                .orElseThrow(() -> new CapsuleException(CapsuleErrorCode.NOT_FOUND));

        Noti notification = notificationConverter.toNoti(fcmSendDto, user, capsule, success);
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Noti notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.setRead(true);  // 읽음 상태로 변경
        }
    }
}
