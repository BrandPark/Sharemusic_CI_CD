package com.brandpark.sharemusic.modules.notification;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum NotificationType {
    FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER;

    public static NotificationType getTypeByName(String typeName) {
        boolean isContain = Arrays.stream(values())
                .map(nt -> nt.name())
                .collect(Collectors.toList()).contains(typeName.toUpperCase());

        if (!isContain) {
            return null;
        }

        return valueOf(typeName.toUpperCase());
    }
}
