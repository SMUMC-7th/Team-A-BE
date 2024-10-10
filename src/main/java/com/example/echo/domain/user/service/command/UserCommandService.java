package com.example.echo.domain.user.service.command;

import com.example.echo.domain.user.converter.UserConverter;
import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.repository.UserReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserReposiotry userRepository;
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
        if(passwordEncoder.matches(dto.password(), user.getPassword())){
            user.updateNickname(dto);
        }else{
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
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
}
