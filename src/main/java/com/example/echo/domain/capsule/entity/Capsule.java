package com.example.echo.domain.capsule.entity;

import com.example.echo.domain.capsule.converter.CapsuleConverter;
import com.example.echo.domain.capsule.entity.enums.TagName;
import com.example.echo.domain.user.entity.User;
import com.example.echo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "capsule")
public class Capsule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "dead_line", nullable = false)
    private LocalDate deadLine;

    @Column(name = "is_opened", nullable = false)
    private boolean isOpened;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_name", columnDefinition = "VARCHAR(15)", nullable = false)
    private TagName tagName;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;

    // 소프트 삭제 메서드
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 복원 메서드
    public void restore() {
        this.deletedAt = null;
    }

    public void open() {
        this.isOpened = true;
    }

    public void update(String title, String content, String tagName) {

        TagName tagNameEnum = CapsuleConverter.toTagName(tagName);

        this.title = title;
        this.content = content;
        this.tagName = tagNameEnum;
    }
    
    public void setImageList(List<Image> images) {
        this.images = images;
    }
    public void setIsOpened(){
        if(LocalDateTime.now().isAfter(deadLine.atStartOfDay())){
            this.isOpened = true;
        }
    }
}
