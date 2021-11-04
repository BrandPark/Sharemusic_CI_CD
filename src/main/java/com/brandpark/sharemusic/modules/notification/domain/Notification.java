package com.brandpark.sharemusic.modules.notification.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String message;

    private boolean checked;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
