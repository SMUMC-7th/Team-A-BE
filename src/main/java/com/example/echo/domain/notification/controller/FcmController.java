package com.example.echo.domain.notification.controller;

import com.example.echo.domain.notification.dto.NotificationDto;
import com.example.echo.domain.notification.service.FcmService;
import com.example.echo.domain.notification.service.FcmTokenService;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.global.apiPayload.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final FcmService fcmService;

    // FCM 토큰 저장 API
    @PostMapping("/token")
    public CustomResponse<String> registerFcmToken(@CurrentUser AuthUser authUser,
                                                   @RequestBody NotificationDto.FcmTokenRequestDto fcmTokenRequestDto) {
        fcmTokenService.saveFcmToken(authUser.getEmail(), fcmTokenRequestDto.fcmToken());
        return CustomResponse.onSuccess("성공적으로 FCM 토큰이 저장되었습니다.");
    }

    // FCM 푸시 알림 전송 API
    @PostMapping("/send")
    public CustomResponse<String> sendNotification(@RequestBody NotificationDto.FcmSendDto fcmSendDto) {
        fcmService.sendFcmNotification(fcmSendDto);
        return CustomResponse.onSuccess("성공적으로 알림이 전송되었습니다.");
    }
}
