package com.brandpark.sharemusic.modules.notification.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class NotificationForm {
    private Long id;
    private String senderProfileImage;
    private String senderNickname;
    private String message;
    private String link;
    private boolean checked;
    private LocalDateTime createdDate;
    private String notificationType;
}
