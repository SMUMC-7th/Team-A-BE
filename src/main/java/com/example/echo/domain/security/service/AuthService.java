package com.example.echo.domain.security.service;


import com.example.echo.domain.security.dto.JwtDto;
import com.example.echo.domain.security.entity.Token;
import com.example.echo.domain.security.exception.SecurityErrorCode;
import com.example.echo.domain.security.repository.TokenRepository;
import com.example.echo.domain.security.utils.JwtUtil;
import com.example.echo.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public Map<String, String> reissueAccessToken(String refreshToken) {

        log.info("[ Auth Service ] 토큰 재발급을 시작합니다.");

        try {
            // Refresh Token에서 사용자 이메일 추출
            String email = jwtUtil.getEmail(refreshToken);
            log.info("[ Auth Service ] Email ---> {}", email);

            // Refresh Token 유효성 검사
            jwtUtil.validateToken(refreshToken);

            // Refresh Token 블랙리스트 확인
            if (tokenService.isTokenBlacklisted(refreshToken)) {
                log.info("[ Auth Service ] 블랙리스트로 등록된 토큰입니다.");
                throw new CustomException(SecurityErrorCode.BLACKLIST_TOKEN);
            }

            // 이메일로 저장된 Refresh Token 가져오기
            String storedRefreshToken = tokenService.getRefreshTokenByEmail(email);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                log.info("[ Auth Service ] 저장된 리프레시 토큰과 일치하지 않습니다.");
                throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

            log.info("[ Auth Service ] Refresh Token이 유효합니다. 새로운 액세스 토큰을 발급합니다.");

            // 새로운 액세스 토큰 발급
            String newAccessToken = jwtUtil.reissueToken(refreshToken);

            // accessToken만 포함한 Map 반환
            return Map.of("accessToken", newAccessToken);

        } catch (SecurityException e) {
            log.error("[ Auth Service ] 보안 예외 발생: {}", e.getMessage());
            throw new SecurityException("유효하지 않은 리프레시 토큰입니다.", e);
        }
    }
}
