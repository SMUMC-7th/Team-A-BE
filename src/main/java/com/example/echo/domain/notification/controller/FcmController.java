package com.example.echo.domain.notification.controller;

import com.example.echo.domain.notification.dto.request.NotificationReqDto;
import com.example.echo.domain.notification.dto.response.NotificationResDto;
import com.example.echo.domain.notification.service.command.FcmService;
import com.example.echo.domain.notification.service.command.FcmTokenService;
import com.example.echo.domain.notification.service.query.FcmQueryService;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.global.apiPayload.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final FcmService fcmService;
    private final FcmQueryService fcmQueryService;

    // FCM 토큰 저장 API
    @PostMapping("/token")
    public CustomResponse<String> registerFcmToken(@CurrentUser AuthUser authUser,
                                                   @RequestBody NotificationReqDto.FcmTokenRequestDto fcmTokenRequestDto) {
        fcmTokenService.saveFcmToken(authUser.getEmail(), fcmTokenRequestDto.fcmToken());
        return CustomResponse.onSuccess("성공적으로 FCM 토큰이 저장되었습니다.");
    }

    // FCM 푸시 알림 전송 API
    @PostMapping("/send")
    public CustomResponse<String> sendNotification(@RequestBody NotificationReqDto.FcmSendDto fcmSendDto) {
        fcmService.sendFcmNotification(fcmSendDto);
        return CustomResponse.onSuccess("성공적으로 알림이 전송되었습니다.");
    }

    // 최신 알림 조회 API
    @GetMapping("")
    public CustomResponse<NotificationResDto.NotificationPreviewListDto> getNotifications(
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "offset", defaultValue = "10") int offset) {
        // Service 계층에서 알림 조회
        NotificationResDto.NotificationPreviewListDto response = fcmQueryService.getNotificationsByCursor(cursor, offset);

        // 성공 응답 반환
        return CustomResponse.onSuccess(response);
    }
}
