package com.example.echo.domain.capsule.converter;

import com.example.echo.domain.capsule.dto.request.CapsuleReqDTO;
import com.example.echo.domain.capsule.entity.Capsule;
import com.example.echo.domain.capsule.entity.Tag;
import com.example.echo.domain.capsule.entity.TagName;
import com.example.echo.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagConverter {
    public static Tag toTag(TagName tagNameEnum) {
        return Tag.builder()
                .tagName(tagNameEnum)
                .build();
    }
}
