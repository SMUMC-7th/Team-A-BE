package com.example.echo.domain.email.service;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultEmailSender {
    private final JavaMailSender mailSender;
    private final String serviceEmail;

    public DefaultEmailSender(
            final JavaMailSender mailSender,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.mailSender = mailSender;
        this.serviceEmail = serviceEmail;
    }

    //회원가입 시 이메일로 "인증 코드"를 전송하는 메서드
    public void sendAuthCodeForSignUp(final String targetEmail, final String authCode) {
        String mailBody = generateEmailBody(authCode); // 이메일 본문 생성
        sendMail("회원가입 인증번호 메일입니다.", targetEmail, mailBody);
    }

    //이메일을 전송하는 메서드
    private void sendMail(final String subject, final String email, final String mailBody) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(subject); //이메일 제목 설정
            helper.setTo(email); //수신자 설정
            helper.setFrom(new InternetAddress(serviceEmail, "Echo")); //발신자 설정
            helper.setText(mailBody, true); //이메일 본문 설정

            mailSender.send(message); //이메일 전송
        } catch (final Exception e) { //전송 중 오류 발생 시 예외 처리
            log.warn("메일 전송 간 오류 발생...", e);
            throw new IllegalArgumentException("메일을 전송할 수 없습니다.");
        }
    }

    //이메일 본문을 동적으로 생성하는 메서드
    private String generateEmailBody(String authCode) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\">" +
                "<title>Another Art</title>" +
                "</head>" +
                "<body>" +
                "<p style=\"font-size:10pt; font-family:sans-serif; padding:0 0 0 10pt\"><br><br></p>" +
                "<div style=\"width:440px; margin:30px auto; padding:40px 0 60px; background-color:#fff; border:1px solid #ddd; text-align:center; font-size:16px; font-family:malgun gothic,serif;\">" +
                "<h3 style=\"font-weight:bold; font-size:20px; margin:28px auto;\">Echo 이메일 본인인증</h3>" +
                "<div style=\"width:200px; margin:28px auto; padding:8px 0 9px; background-color:#f4f4f4; border-radius:3px;\">" +
                "<span style=\"display:inline-block; vertical-align:middle; font-size:13px; color:#666;\">인증번호</span>" +
                "<span style=\"display:inline-block; margin-left:16px; vertical-align:middle; font-size:21px; font-weight:bold; color:#4d5642;\">" + authCode + "</span>" +
                "</div>" +
                "<p style=\"text-align:center; font-size:13px; color:#000; line-height:1.6; margin-top:40px; margin-bottom:0;\">" +
                "해당 인증번호를 인증 번호 확인란에 기입하여 주세요.<br>" +
                "Echo를 이용해 주셔서 감사합니다.<br>" +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
