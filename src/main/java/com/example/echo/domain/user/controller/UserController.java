package com.example.echo.domain.user.controller;


import com.example.echo.domain.security.dto.LoginRequestDto;
import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.service.command.UserCommandService;
import com.example.echo.domain.user.service.query.UserQueryService;
import com.example.echo.global.apiPayload.CustomResponse;
import com.example.echo.global.apiPayload.exception.CustomException;
import jakarta.validation.Valid;
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
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDTO){
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        return null;
    }


    @PostMapping("/signup")
    public CustomResponse<UserResDto.UserResponseDto> createUser(@RequestBody UserReqDto.CreateUserRequestDto dto){
        UserResDto.UserResponseDto result = userCommandService.createUser(dto);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }


    @GetMapping
    public CustomResponse<UserResDto.UserResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails){
        UserResDto.UserResponseDto result = userQueryService.getUser(userDetails.getUsername());
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }


    @PatchMapping
    public CustomResponse<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        userCommandService.deleteUser(userDetails.getUsername());
        return CustomResponse.onSuccess(HttpStatus.OK, "성공적으로 회원이 탈퇴되었습니다.");
    }

    @PatchMapping("/nickname")
    public CustomResponse<?> updateNickname(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody UserReqDto.UpdateNicknameRequestDto updateNicknameRequestDto){
        userCommandService.updateNickname(userDetails.getUsername(), updateNicknameRequestDto);
        return CustomResponse.onSuccess(HttpStatus.OK, "닉네임 변경이 완료되었습니다.");
    }

    @PutMapping("/password")
    public CustomResponse<?> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody UserReqDto.UpdatePasswordRequestDto updatePasswordRequestDto){
        userCommandService.updatePassword((userDetails.getUsername()), updatePasswordRequestDto);
        return CustomResponse.onSuccess(HttpStatus.OK, "비밀번호 변경이 완료되었습니다.");
    }

}
