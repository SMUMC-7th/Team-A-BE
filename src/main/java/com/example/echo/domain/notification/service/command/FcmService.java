package com.example.echo.domain.notification.service.command;

import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.exception.code.CapsuleErrorCode;
import com.example.echo.domain.capsule.exception.handler.CapsuleException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

    // 매일 오전 10시에 실행
    @Scheduled(cron = "0 0 10 * * ?") // 초, 분, 시, 일, 월, 요일
    public void scheduleDeadlineNotifications() {
        log.info("스케줄러가 deadLine 푸시 알림 작업을 실행합니다.");
        sendDeadlineNotifications();
    }

    // 페이징 기반 deadLine 처리
    private void sendDeadlineNotifications() {
        LocalDate today = LocalDate.now();
        int pageSize = 100; // 한 번에 처리할 데이터 개수
        int page = 0;

        // 페이징 반복 처리
        Slice<Capsule> capsulesToNotify;
        do {
            Pageable pageable = PageRequest.of(page, pageSize); // 페이지 요청 생성
            capsulesToNotify = capsuleRepository.findByDeadLineAndIsOpenedFalse(today, pageable);

            // 조회된 캡슐 처리
            capsulesToNotify.forEach(this::processCapsule);

            page++; // 다음 페이지로 이동
        } while (capsulesToNotify.hasNext()); // 다음 페이지가 있을 때 계속 실행
    }

    // 개별 캡슐 처리 메서드
    private void processCapsule(Capsule capsule) {
        try {
            sendCapsuleNotification(capsule);
            capsule.open(); // 열림 처리
            capsuleRepository.save(capsule);
            log.info("캡슐 ID {}에 대해 알림 전송 및 열림 상태로 업데이트 완료.", capsule.getId());
        } catch (Exception e) {
            log.error("캡슐 ID {} 처리 중 오류 발생: {}", capsule.getId(), e.getMessage(), e);
        }
    }

    // 알림 전송 로직 분리
    private void sendCapsuleNotification(Capsule capsule) {
        // 제목과 메시지 생성
        String title = String.format("[%s] 타임캡슐이 열렸어요!", capsule.getTitle());
        String body = "지금 눌러서 확인해보세요!";

        NotificationReqDto.FcmSendDto notificationDto = NotificationReqDto.FcmSendDto.builder()
                .userId(capsule.getUser().getId())
                .capsuleId(capsule.getId())
                .title(title) // 동적 제목
                .body(body)   // 고정 메시지
                .build();

        sendFcmNotification(notificationDto);
    }
}
