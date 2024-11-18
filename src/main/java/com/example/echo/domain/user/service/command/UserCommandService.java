package com.example.echo.domain.user.service.command;

import com.example.echo.domain.security.dto.JwtDto;
import com.example.echo.domain.security.userDetails.CustomUserDetails;
import com.example.echo.domain.security.utils.JwtUtil;
import com.example.echo.domain.user.converter.UserConverter;
import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.entity.AuthType;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.repository.UserReposiotry;
import com.example.echo.global.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserReposiotry userRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 중복 이메일 처리
    private void validateEmail(String email){
        // 이미 있는 이메일의 유저 에러처리
        Optional<User> _user = userRepository.findByEmail(email);
        if(_user.isPresent()){
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    public UserResDto.UserResponseDto createUser(UserReqDto.CreateUserRequestDto dto){
        validateEmail(dto.email());
        User user = UserConverter.toEntity(dto, passwordEncoder);
        userRepository.save(user);
        return UserConverter.from(user);
    }


    public void deleteUser(HttpServletRequest request, String email){
        try {
            // 사용자 정보 조회
            User user = userRepository.findByEmailAndActiveTrue(email)
                    .orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));

            // Soft delete 처리
            user.softDelete();

            // 로그아웃 및 토큰 무효화 처리
            String accessToken = jwtUtil.resolveAccessToken(request);

            if (accessToken != null) {
                String accessTokenKey = email + ":blacklist";

                // Redis 블랙리스트 Access 토큰 추가
                redisUtil.setBlackList(accessTokenKey, accessToken, 30 * 60 * 1000L); // 30분 동안 블랙리스트에 추가
                log.info("[ UserService ] Access 토큰 블랙리스트 등록: {}", accessToken);

                // refreshToken Redis에서 삭제
                String refreshTokenKey = email + ":refresh";
                String refreshToken = (String) redisUtil.get(refreshTokenKey);

                if (refreshToken != null) {
                    redisUtil.delete(refreshTokenKey);
                    log.info("[ UserService ] 리프레시 토큰 삭제: {}", refreshToken);
                } else {
                    log.warn("[ UserService ] 리프레시 토큰이 존재하지 않습니다.");
                }
            }

        } catch (Exception e) {
            log.error("[ UserService ] 사용자 Soft Delete 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("회원 탈퇴 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    public void performHardDelete() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(30);  // 매일 새벽 2시 기준 30일 전 날짜
        userRepository.findByDeletedAtBefore(expiryDate);  // deletedAt이 expiryDate보다 작거나 같은 모든 user삭제
    }

    public void updateNickname(String email, UserReqDto.UpdateNicknameRequestDto dto){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        user.updateNickname(dto.newNickname());
    }

    public void updatePassword(String email, UserReqDto.UpdateAuthPasswordRequestDto dto){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        if(passwordEncoder.matches(dto.newPassword(), user.getPassword())){
            throw new UserException(UserErrorCode.PASSWORD_UNCHANGED);
        }
        if(passwordEncoder.matches(dto.oldPassword(), user.getPassword())){
            String newEncodedPassword = passwordEncoder.encode(dto.newPassword());
            user.setPassword(newEncodedPassword);
        }else{
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
    }

    public void setPassword(String email, String password){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));

        if(passwordEncoder.matches(password, user.getPassword())){
            throw new UserException(UserErrorCode.PASSWORD_UNCHANGED);
        }
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }


    public JwtDto auth(UserReqDto.OAuthUserRequestDto dto, AuthType authType){
        // soft delete 된 유저 포함해서 전체 user email로 찾기
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());
        // 소셜 회원가입
        if(optionalUser.isEmpty()){
            User newUser = UserConverter.toEntity(dto, authType);
            userRepository.save(newUser);
            return provideToken(newUser);
        }

        User user = optionalUser.get();
        // active = false인 유저 에러 처리
        if (!user.isActive()) {
            throw new UserException(UserErrorCode.USER_INACTIVE);
        }
        if(!user.getAuthType().equals(authType)){
            throw new UserException(UserErrorCode.WRONG_AUTH_TYPE);
        }

        // 로그인 처리
        return provideToken(user);
    }

    // 토큰 생성 메서드
    public JwtDto provideToken(User user){
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        // 토큰 생성
        JwtDto jwtDto = JwtDto.builder()
                .accessToken(jwtUtil.createJwtAccessToken(customUserDetails)) //access token 생성
                .refreshToken(jwtUtil.createJwtRefreshToken(customUserDetails)) //refresh token 생성
                .build();
        return jwtDto;
    }






}