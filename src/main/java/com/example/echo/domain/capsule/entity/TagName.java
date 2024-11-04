package com.example.echo.domain.capsule.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TagName {
    MEMORY("기억"),
    GROWTH("성장"),
    GRATITUDE("감사"),
    GOAL("목표"),
    HAPPINESS("행복"),
    ADVENTURE("모험"),
    FAMILY("가족"),
    FRIEND("친구"),
    LEARNING("배움"),
    COMFORT("위로");

    private final String value;

    TagName(String displayName) {
        this.value = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return value;
    }

    public static TagName fromDisplayName(String displayName) {
        for (TagName tagName : TagName.values()) {
            if (tagName.getDisplayName().equals(displayName)) {
                return tagName;
            }
        }
        throw new IllegalArgumentException("Unknown tag name: " + displayName);
    }
}
