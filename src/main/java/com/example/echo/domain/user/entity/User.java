package com.example.echo.domain.user.entity;

import com.example.echo.domain.user.dto.request.UserReqDto;
import com.example.echo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 소프트 삭제 메서드
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    // 복원 메서드
    public void restore() {
        this.deletedAt = null;
    }

    public void updateNickname(UserReqDto.UpdateNicknameRequestDto dto) {
        this.nickname = dto.newNickname();
    }

    public void setPassword(String newEncodedPassword) {
        password = newEncodedPassword;
    }
}
