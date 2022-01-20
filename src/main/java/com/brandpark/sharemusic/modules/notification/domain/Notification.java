package com.brandpark.sharemusic.modules.notification.domain;

import com.brandpark.sharemusic.modules.BaseTimeEntity;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import lombok.*;

import javax.persistence.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean checked;

    @Column(nullable = false)
    private String link;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    public void check() {
        if (!checked) {
            checked = true;
        }
    }
}
