package com.example.echo.domain.security.global.filter;

import com.example.echo.domain.security.exception.SecurityErrorCode;
import com.example.echo.domain.security.utils.JwtUtil;
import com.example.echo.global.apiPayload.CustomResponse;
import com.example.echo.global.util.HttpResponseUtil;
import com.example.echo.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            String accessToken = jwtUtil.resolveAccessToken(request);

            if (accessToken == null) {
                log.warn("[ CustomLogoutHandler ] Access Token 이 없습니다.");
                setErrorResponse(response, SecurityErrorCode.BAD_CREDENTIALS);
                return;
            }
            // 토큰 유효성 검증
            jwtUtil.validateToken(accessToken);

            String email = jwtUtil.getEmail(accessToken);
            String accessTokenKey = email + ":blacklist";

            // Redis 블랙리스트 Access 토큰 추가
            // 30분
            redisUtil.setBlackList(accessTokenKey, accessToken, 30*60*1000L);
            log.info("[ CustomLogoutHandler ] Access 토큰 블랙리스트 등록");

            // refresh 토큰 가져오기
            String refreshTokenKey = email + ":refresh";
            String refreshToken = (String) redisUtil.get(refreshTokenKey);

            if (refreshToken != null) {
                redisUtil.delete(refreshTokenKey);
                log.info("[ CustomLogoutHandler ] 리프레시 토큰 삭제");
            } else {
                log.warn("[ CustomLogoutHandler ] 리프레시 토큰이 존재하지 않습니다.");
                setErrorResponse(response, SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
                return;
            }

            // FCM 토큰 삭제
            String fcmTokenKey = "FCM_TOKEN:" + email;
            if (redisUtil.get(fcmTokenKey) != null) {
                redisUtil.delete(fcmTokenKey);
                log.info("[ CustomLogoutHandler ] FCM 토큰 삭제");
            } else {
                log.warn("[ CustomLogoutHandler ] FCM 토큰이 존재하지 않습니다.");
            }

            HttpResponseUtil.setSuccessResponse(response, HttpStatus.OK, "로그아웃이 완료되었습니다.");

        } catch (ExpiredJwtException e) {
            log.warn("[ CustomLogoutHandler ] Access Token 이 만료되었습니다.");
            setErrorResponse(response, SecurityErrorCode.TOKEN_EXPIRED);
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("[ CustomLogoutHandler ] 유효하지 않은 토큰입니다.");
            setErrorResponse(response, SecurityErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("[ CustomLogoutHandler ] 로그아웃 처리 중 오류 발생: {}", e.getMessage());
            setErrorResponse(response, SecurityErrorCode.INTERNAL_SECURITY_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, SecurityErrorCode errorCode) {
        try {
            CustomResponse<Void> customResponse = CustomResponse.onFailure(errorCode.getCode(), errorCode.getMessage());
            HttpResponseUtil.setErrorResponse(response, errorCode.getStatus(), customResponse);
        } catch (IOException e) {
            log.error("[ CustomLogoutHandler ] 응답 처리 중 IOException 발생: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}