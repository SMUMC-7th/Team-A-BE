package com.example.echo.domain.security.service;

import com.example.echo.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisUtil redisUtil;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final long BLACKLIST_TTL_MS = 2 * 24 * 3600 * 1000L; // 2일

    //주어진 토큰을 블랙리스트에 추가하고, 설정한 TTL만큼 유지
    public void addToBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisUtil.save(key, true, BLACKLIST_TTL_MS, TimeUnit.MILLISECONDS);
    }

    //이메일로 Redis에서 토큰 조회
    public String getRefreshTokenByEmail(String email) {
        return (String) redisUtil.get(email + ":refresh");
    }

    //이메일로 Redis에서 토큰 삭제
    public void deleteTokenByEmail(String email) {
        redisUtil.delete(email + ":refresh");
    }

    // 주어진 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token; // 블랙리스트 키 생성
        return redisUtil.hasKey(key); // 블랙리스트에 토큰이 있는지 확인(Boolean.TRUE.equals 를 사용하여 null포인터 예외 방지)
    }
}