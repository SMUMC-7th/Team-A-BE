package com.example.echo.domain.email.service;


import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailAuthService {
    private final RedisUtil redisUtil;
    private final DefaultEmailSender emailSender;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis; //인증 코드의 유효시간

    //이메일 인증 코드를 생성하고 전송하는 메서드
    public void sendEmailAuthCode(String userEmail, String email) {
        if(!userEmail.equals(email)){
            throw new UserException(UserErrorCode.INVALID_EMAIL);
        }
        String authCode = createCode(); //인증 코드 생성
        emailSender.sendAuthCodeForSignUp(email, authCode); //email로 인증코드 전송
        redisUtil.save(email, authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS); //Redis에 인증 코드 저장
    }

    //이메일 인증 코드를 검증하는 메서드
    public void verifyEmailAuthCode(String email, String authCode) {
        //Redis에 저장된 인증 코드 가져오기
        String storedAuthCode = (String) redisUtil.get(email);

        //저장된 인증 코드와 제출된 인증 코드가 일치하지 않으면 예외 발생
        if (!authCode.equals(storedAuthCode)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }
        redisUtil.delete(email); //인증 성공 시 Redis에서 인증 코드 삭제
    }

    //인증 코드 생성 메서드
    private String createCode() {
        int length = 6;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((int) (Math.random() * 10)); //0~9 사이의 숫자를 랜덤하게 생성
        }
        return builder.toString(); //생성된 인증 코드 반환
    }
}