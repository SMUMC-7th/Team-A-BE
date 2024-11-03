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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserReposiotry userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserResDto.UserResponseDto createUser(UserReqDto.CreateUserRequestDto dto){
        User user = UserConverter.toEntity(dto, passwordEncoder);
        userRepository.save(user);
        return UserConverter.from(user);
    }

    public void deleteUser(String email){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        user.softDelete();
    }

    public void updateNickname(String email, UserReqDto.UpdateNicknameRequestDto dto){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        user.updateNickname(dto.newNickname());
    }

    public void updatePassword(String email, UserReqDto.UpdatePasswordRequestDto dto){
        User user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        if(passwordEncoder.matches(dto.oldPassword(), user.getPassword())){
            String newEncodedPassword = passwordEncoder.encode(dto.newPassword());
            user.setPassword(newEncodedPassword);
        }else{
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
    }



    public JwtDto auth(UserReqDto.OAuthUserRequestDto dto, AuthType authType){
        // soft delete 된 유저 포함해서 전체 user email로 찾기
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());
        // 네이버 회원가입
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
