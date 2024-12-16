package com.example.echo.domain.reply.entity;


import com.example.echo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "reply")
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref")
    private Long ref;

    @Column(name = "ref_step")
    private Long ref_step;

    @Column(name = "ref_level")
    private Long ref_level;

    @Column(name = "content")
    private String content;

}
