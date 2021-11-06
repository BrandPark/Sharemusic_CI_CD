package com.brandpark.sharemusic.modules.notification.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class NotificationForm {
    private String senderProfileImage;
    private String senderNickname;
    private String message;
    private LocalDateTime createdDate;
    private String notificationType;
}
