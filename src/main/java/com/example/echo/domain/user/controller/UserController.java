package com.example.echo.domain.user.controller;


import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.dto.JwtDto;
import com.example.echo.domain.security.dto.LoginRequestDto;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.entity.AuthType;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.service.command.UserCommandService;
import com.example.echo.domain.user.service.query.UserQueryService;
import com.example.echo.global.apiPayload.CustomResponse;
import com.example.echo.global.apiPayload.exception.handler.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "유저 API")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "LOGIN400", description = "올바르지 않은 요청입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "LOGIN401", description = "잘못된 정보입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDTO) {
        return null;
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "SEC404_0", description = "리프레시 토큰이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public ResponseEntity<?> logout() {
        return null;
    }


    @PostMapping("/signup")
    @Operation(summary = "회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "CREATED, 성공"),
            @ApiResponse(responseCode = "USER409_1", description = "이미 존재하는 이메일입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "COMMON500", description = "서버 내부 오류가 발생했습니다",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<UserResDto.UserResponseDto> createUser(@RequestBody UserReqDto.CreateUserRequestDto dto) {
        UserResDto.UserResponseDto result = userCommandService.createUser(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @PostMapping("/kakao")
    @Operation(summary = "카카오 회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER400_3", description = "잘못된 인증 방식입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER403_1", description = "계정이 비활성화 상태입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<JwtDto> kakao(@RequestBody UserReqDto.OAuthUserRequestDto dto) {
        JwtDto result = userCommandService.auth(dto, AuthType.KAKAO);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PostMapping("/naver")
    @Operation(summary = "네이버 회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER400_3", description = "잘못된 인증 방식입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER403_1", description = "계정이 비활성화 상태입니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<JwtDto> naver(@RequestBody UserReqDto.OAuthUserRequestDto dto) {
        JwtDto result = userCommandService.auth(dto, AuthType.NAVER);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }


    @GetMapping
    @Operation(summary = "회원 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<UserResDto.UserResponseDto> getUser(@CurrentUser AuthUser authUser) {
        UserResDto.UserResponseDto result = userQueryService.getUser(authUser.getEmail());
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }


    @PatchMapping
    @Operation(summary = "회원 탈퇴 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<?> deleteUser(HttpServletRequest request,
                                        @CurrentUser AuthUser authUser) {
        userCommandService.deleteUser(request, authUser.getEmail());
        return CustomResponse.onSuccess(HttpStatus.OK, "성공적으로 회원이 탈퇴되었습니다.");
    }

    @PatchMapping("/nickname")
    @Operation(summary = "회원 닉네임 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "SEC401_0", description = "인증 자격 증명이 제공되지 않았거나 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<?> updateNickname(@CurrentUser AuthUser authUser,
                                            @RequestBody UserReqDto.UpdateNicknameRequestDto updateNicknameRequestDto) {
        userCommandService.updateNickname(authUser.getEmail(), updateNicknameRequestDto);
        return CustomResponse.onSuccess(HttpStatus.OK, "닉네임 변경이 완료되었습니다.");
    }

    @PutMapping("/auth/password")
    @Operation(summary = "회원 비밀번호 수정 API", description = "마이페이지에서 비밀번호 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER400_1", description = "oldPassword가 기존 비밀번호가 다릅니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER400_2", description = "newPassword가 기존이랑 같습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<?> updateAuthPassword(@CurrentUser AuthUser authUser,
                                            @RequestBody UserReqDto.UpdateAuthPasswordRequestDto dto) {
        userCommandService.updatePassword(authUser.getEmail(), dto);
        return CustomResponse.onSuccess(HttpStatus.OK, "비밀번호 변경이 완료되었습니다.");
    }

    @PutMapping("/password")
    @Operation(summary = "회원 비밀번호 수정 API", description = "비밀번호 찾기에서 이메일 인증 후 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK, 성공"),
            @ApiResponse(responseCode = "USER400_1", description = "oldPassword가 기존 비밀번호가 다릅니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER400_2", description = "newPassword가 기존이랑 같습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "USER404_1", description = "사용자 데이터 값이 존재하지 않습니다.",content = @Content(schema = @Schema(implementation = CustomResponse.class))),
    })
    public CustomResponse<?> updatePassword(@RequestBody UserReqDto.UpdatePasswordRequestDto dto) {
        userCommandService.setPassword(dto.email(), dto.password());
        return CustomResponse.onSuccess(HttpStatus.OK, "비밀번호 변경이 완료되었습니다.");
    }
}
