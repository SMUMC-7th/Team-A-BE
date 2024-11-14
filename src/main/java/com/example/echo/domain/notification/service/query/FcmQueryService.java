package com.example.echo.domain.notification.service.query;

import com.example.echo.domain.notification.converter.NotificationConverter;
import com.example.echo.domain.notification.dto.response.NotificationResDto;
import com.example.echo.domain.notification.entity.Noti;
import com.example.echo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationResDto.NotificationPreviewListDto getNotificationsByCursor(Long cursor, int offset) {
        Pageable pageable = PageRequest.of(0, offset);

        // createdAt을 기준으로 최신순 조회
        Slice<Noti> notifications = notificationRepository.findNotificationsByCreatedAtCursor(cursor, pageable);

        // 다음 커서 계산
        Long nextCursor = notifications.hasNext()
                ? notifications.getContent().get(notifications.getNumberOfElements() - 1).getId()
                : null;

        // 변환하여 반환
        return NotificationConverter.toNotificationResponse(notifications, nextCursor);
    }
}
