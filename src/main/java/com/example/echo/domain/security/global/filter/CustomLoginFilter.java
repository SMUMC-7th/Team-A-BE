package com.example.echo.domain.security.global.filter;

import com.example.echo.domain.security.dto.JwtDto;
import com.example.echo.domain.security.dto.LoginRequestDto;
import com.example.echo.domain.security.userDetails.CustomUserDetails;
import com.example.echo.domain.security.utils.JwtUtil;
import com.example.echo.domain.user.service.command.UserCommandService;
import com.example.echo.global.apiPayload.CustomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserCommandService userCommandService;

    //로그인 시도 메서드
    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response) throws AuthenticationException {

        log.info("[ Login Filter ]  로그인 시도 : Custom Login Filter 작동 ");
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto requestBody;
        try {
            requestBody = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("[ Login Filter ] Request Body 파싱 과정에서 오류가 발생했습니다.");
        }

        //Request Body 에서 추출
        String email = requestBody.email(); //Email 추출
        String password = requestBody.password(); //password 추출
        log.info("[ Login Filter ]  Email ---> {} ", email);
        log.info("[ Login Filter ]  Password ---> {} ", password);

        // 탈퇴한지 30일 지나지 않은 유저 복구
        userCommandService.restoreUser(email);

        //UserNamePasswordToken 생성 (인증용 객체)
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(email, password, null);

        log.info("[ Login Filter ] 인증용 객체 UsernamePasswordAuthenticationToken 이 생성되었습니다. ");
        log.info("[ Login Filter ] 인증을 시도합니다.");

        //인증 시도
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain,
            @NonNull Authentication authentication) throws IOException {


        log.info("[ Login Filter ] 로그인에 성공 하였습니다.");

        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();


        //Client 에게 줄 Response 를 Build
        JwtDto jwtDto = JwtDto.builder()
                .accessToken(jwtUtil.createJwtAccessToken(customUserDetails)) //access token 생성
                .refreshToken(jwtUtil.createJwtRefreshToken(customUserDetails)) //refresh token 생성
                .build();

        // CustomResponse 작성
        CustomResponse<JwtDto> customResponse = CustomResponse.onSuccess(jwtDto);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());  // Response의 Status를 200으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Body에 토큰을 담은 Response 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }

    //로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException failed) throws IOException {

        log.info("[ Login Filter ] 로그인에 실패하였습니다.");

        String errorCode;
        String errorMessage;

        if (failed instanceof BadCredentialsException) {
            errorCode = "LOGIN401";
            errorMessage = "잘못된 정보입니다.";
        } else if (failed instanceof LockedException) {
            errorCode = "LOGIN423";
            errorMessage = "계정이 잠금 상태입니다.";
        } else if (failed instanceof DisabledException) {
            errorCode = "LOGIN403";
            errorMessage = "계정이 비활성화 되었습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            errorCode = "LOGIN404";
            errorMessage = "계정을 찾을 수 없습니다.";
        } else if (failed instanceof AuthenticationServiceException) {
            errorCode = "LOGIN400";
            errorMessage = "Request Body 파싱 중 오류가 발생했습니다.";
        } else {
            errorCode = "LOGIN500";
            errorMessage = "인증에 실패했습니다.";
        }

        // CustomResponse 생성 (데이터는 null로 설정)
        CustomResponse<Void> customResponse = CustomResponse.onFailure(errorCode, errorMessage);

        // Response 작성
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Body에 CustomResponse 담아 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(customResponse));
    }
}