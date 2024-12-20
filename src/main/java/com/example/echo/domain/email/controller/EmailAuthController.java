package com.example.echo.domain.email.controller;


import com.example.echo.domain.email.dto.EmailRequestDto;
import com.example.echo.domain.email.service.EmailAuthService;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "이메일 인증 API 입니다.")
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    @Operation(method = "POST", summary = "회원가입 시 이메일에 인증 코드 전송", description = "이메일에 인증 코드를 전송합니다. 회원가입 시 사용.")
    @PostMapping("/send/sign-up")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER409_1", description = "이미 존재하는 이메일입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<String> sendEmailAuthCodeToSignUp(@RequestBody EmailRequestDto.SendEmailRequestDto dto) {
        emailAuthService.sendEmailAuthCodeToSignUp(dto.email()); // 이메일 인증 코드 전송 로직을 실행
        return CustomResponse.onSuccess("해당 이메일에 인증 코드기 전송되었습니다.");
    }

    @Operation(method = "POST", summary = "비밀번호 찾기 시 이메일에 인증 코드 전송", description = "이메일에 인증 코드를 전송합니다. 비밀번호 변경 시 사용.")
    @PostMapping("/send/password")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<String> sendEmailAuthCodeToFindPW(@RequestBody EmailRequestDto.SendEmailRequestDto dto) {
        emailAuthService.sendEmailAuthCodeToFindPW(dto.email()); // 이메일 인증 코드 전송 로직을 실행
        return CustomResponse.onSuccess("해당 이메일에 인증 코드기 전송되었습니다.");
    }

    @Operation(method = "POST", summary = "인증 코드 검증", description = "사용자가 작성한 인증 코드를 검증합니다.")
    @PostMapping("/verify")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USE400_6", description = "인증코드가 일치하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<?> verifyEmailAuthCode(@RequestBody EmailRequestDto.VerifyEmailRequestDto dto) {
        // 이메일 인증 코드를 검증하는 요청을 처리합니다.
        emailAuthService.verifyEmailAuthCode(dto.email(), dto.code()); // 이메일 인증 코드 검증 로직을 실행
        return CustomResponse.onSuccess("성공적으로 이메일 인증이 완료되었습니다.");
    }
}
