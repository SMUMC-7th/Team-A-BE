package com.example.echo.domain.notification.controller;

import com.example.echo.domain.notification.dto.request.NotificationReqDto;
import com.example.echo.domain.notification.dto.response.NotificationResDto;
import com.example.echo.domain.notification.service.command.FcmService;
import com.example.echo.domain.notification.service.command.FcmTokenService;
import com.example.echo.domain.notification.service.query.FcmQueryService;
import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관련 API", description = "알림 관련 API입니다.")
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final FcmService fcmService;
    private final FcmQueryService fcmQueryService;

    // FCM 토큰 저장 API
    @Operation(summary = "FCM 토큰 저장", description = "로그인된 사용자의 FCM 토큰을 저장합니다.")
    @PostMapping("/token")
    public CustomResponse<String> registerFcmToken(@CurrentUser AuthUser authUser,
                                                   @RequestBody NotificationReqDto.FcmTokenRequestDto fcmTokenRequestDto) {
        fcmTokenService.saveFcmToken(authUser.getEmail(), fcmTokenRequestDto.fcmToken());
        return CustomResponse.onSuccess("성공적으로 FCM 토큰이 저장되었습니다.");
    }

    // FCM 푸시 알림 전송 API
    @Operation(summary = "FCM 푸시 알림 전송", description = "지정된 FCM 토큰으로 푸시 알림을 전송합니다.")
    @PostMapping("/send")
    public CustomResponse<String> sendNotification(@RequestBody NotificationReqDto.FcmSendDto fcmSendDto) {
        fcmService.sendFcmNotification(fcmSendDto);
        return CustomResponse.onSuccess("성공적으로 알림이 전송되었습니다.");
    }

    // 최신 알림 조회 API
    @Operation(summary = "최신 알림 조회", description = "커서 기반 페이징을 사용하여 최신 알림 목록을 조회합니다.")
    @GetMapping("")
    public CustomResponse<NotificationResDto.NotificationPreviewListDto> getNotifications(
            @Parameter(description = "다음 페이지 조회에 사용할 커서 값")
            @RequestParam(value = "cursor", required = false) Long cursor,

            @Parameter(description = "한 페이지에 조회할 알림 개수")
            @RequestParam(value = "offset", defaultValue = "10") int offset) {
        // Service 계층에서 알림 조회
        NotificationResDto.NotificationPreviewListDto response = fcmQueryService.getNotificationsByCursor(cursor, offset);

        // 성공 응답 반환
        return CustomResponse.onSuccess(response);
    }

    @Operation(summary = "알림 읽음 처리", description = "알림 ID에 해당하는 알림을 읽음 상태로 업데이트합니다.")
    @PatchMapping("/{id}/read")
    public CustomResponse<String> markNotificationAsRead(@Parameter(description = "읽음 처리할 알림 ID") @PathVariable Long id) {
        fcmService.markAsRead(id);
        return CustomResponse.onSuccess("알림이 읽음 상태로 업데이트되었습니다.");
    }
}
