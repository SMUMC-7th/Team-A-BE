package com.example.echo.domain.user.controller;


import com.example.echo.domain.security.annotation.CurrentUser;
import com.example.echo.domain.security.dto.LoginRequestDto;
import com.example.echo.domain.security.entity.AuthUser;
import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.service.command.UserCommandService;
import com.example.echo.domain.user.service.query.UserQueryService;
import com.example.echo.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDTO){
        return null;
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<?> logout(){
        return null;
    }


    @PostMapping("/signup")
    @Operation(summary = "회원가입 API")
    public CustomResponse<UserResDto.UserResponseDto> createUser(@RequestBody UserReqDto.CreateUserRequestDto dto){
        UserResDto.UserResponseDto result = userCommandService.createUser(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }


    @GetMapping
    @Operation(summary = "회원 조회 API")
    public CustomResponse<UserResDto.UserResponseDto> getUser(@CurrentUser AuthUser authUser){
        UserResDto.UserResponseDto result = userQueryService.getUser(authUser.getEmail());
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }


    @PatchMapping
    @Operation(summary = "회원 탈퇴 API")
    public CustomResponse<?> deleteUser(@AuthenticationPrincipal @CurrentUser AuthUser authUser){
        userCommandService.deleteUser(authUser.getEmail());
        return CustomResponse.onSuccess(HttpStatus.OK, "성공적으로 회원이 탈퇴되었습니다.");
    }

    @PatchMapping("/nickname")
    @Operation(summary = "회원 닉네임 수정 API")
    public CustomResponse<?> updateNickname(@CurrentUser AuthUser authUser,
                                            @RequestBody UserReqDto.UpdateNicknameRequestDto updateNicknameRequestDto){
        userCommandService.updateNickname(authUser.getEmail(), updateNicknameRequestDto);
        return CustomResponse.onSuccess(HttpStatus.OK, "닉네임 변경이 완료되었습니다.");
    }

    @PutMapping("/password")
    @Operation(summary = "회원 비밀번호 수정 API")
    public CustomResponse<?> updatePassword(@CurrentUser AuthUser authUser,
                                            @RequestBody UserReqDto.UpdatePasswordRequestDto updatePasswordRequestDto){
        userCommandService.updatePassword(authUser.getEmail(), updatePasswordRequestDto);
        return CustomResponse.onSuccess(HttpStatus.OK, "비밀번호 변경이 완료되었습니다.");
    }

}
