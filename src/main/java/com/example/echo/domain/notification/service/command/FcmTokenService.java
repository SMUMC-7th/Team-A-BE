package com.example.echo.domain.notification.service.command;

import com.example.echo.domain.notification.exception.NotificationErrorCode;
import com.example.echo.domain.notification.exception.NotificationException;
import com.example.echo.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmTokenService {

    private static final long TOKEN_EXPIRATION_DAYS = 30L;  // 토큰 만료일 상수
    private final RedisUtil redisUtil;

    // FCM 토큰 저장
    @Transactional
    public void saveFcmToken(String email, String fcmToken) {
        if (hasFcmToken(email)) {
            throw new NotificationException(NotificationErrorCode.FCMTOKEN_ALREADY_EXIST);
        }
        saveFcmTokenInRedis(email, fcmToken);
    }

    // FCM 토큰 삭제
    public void deleteFcmToken(String email) {
        String redisKey = generateRedisKey(email);
        boolean deleted = redisUtil.delete(redisKey);

        if (deleted) {
            log.info("[FcmTokenService] 사용자 {}의 FCM 토큰이 삭제되었습니다.", email);
        } else {
            log.warn("[FcmTokenService] 사용자 {}의 FCM 토큰을 찾을 수 없습니다.", email);
        }
    }

    // Redis 키 생성 로직
    private String generateRedisKey(String email) {
        return "FCM_TOKEN:" + email;
    }

    // FCM 토큰 존재 여부 확인
    public boolean hasFcmToken(String email) {
        String redisKey = generateRedisKey(email);
        return redisUtil.hasKey(redisKey);
    }

    // Redis에 FCM 토큰 저장
    private void saveFcmTokenInRedis(String email, String fcmToken) {
        String redisKey = generateRedisKey(email);
        redisUtil.save(redisKey, fcmToken, TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
        log.info("[FcmTokenService] 사용자 {}의 FCM 토큰이 저장되었습니다.", email);
    }
}
