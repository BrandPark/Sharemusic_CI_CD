package com.brandpark.sharemusic.api.v1.notification.dto;

import com.brandpark.sharemusic.modules.notification.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationInfoResponse {
    private Long id;
    private String senderProfileImage;
    private String senderNickname;
    private String message;
    private String link;
    private boolean checked;
    private LocalDateTime createdDate;
    private NotificationType notificationType;
}
