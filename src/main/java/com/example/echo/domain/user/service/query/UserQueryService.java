package com.example.echo.domain.user.service.query;

import com.example.echo.domain.user.converter.UserConverter;
import com.example.echo.domain.user.dto.response.UserResDto;
import com.example.echo.domain.user.entity.User;
import com.example.echo.domain.user.exception.UserErrorCode;
import com.example.echo.domain.user.exception.UserException;
import com.example.echo.domain.user.repository.UserReposiotry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserReposiotry userReposiotry;

    public UserResDto.UserResponseDto getUser(String email){
        User user = userReposiotry.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserException(UserErrorCode.NO_USER_DATA_REGISTERED));
        return UserConverter.from(user);
    }


}
