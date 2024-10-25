package com.example.echo.domain.security.controller;

import com.example.echo.domain.security.dto.JwtDto;
import com.example.echo.domain.security.service.AuthService;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "토큰 발급 API", description = "토큰 발급 API입니다.")
public class AuthController {

    private final AuthService authService;

    //토큰 재발급 API
    @Operation(method = "POST", summary = "토큰 재발급", description = "토큰 재발급. accessToken과 refreshToken을 body에 담아서 전송합니다.")
    @PostMapping("/reissue")
    public CustomResponse<?> reissue(@RequestBody JwtDto jwtDto) {
        JwtDto response = authService.reissueAccessToken(jwtDto.refreshToken());
        log.info("[ Auth Controller ] 토큰을 재발급합니다. ");

        return CustomResponse.onSuccess(response);
    }
}