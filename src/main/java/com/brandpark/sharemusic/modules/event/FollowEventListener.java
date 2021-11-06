package com.brandpark.sharemusic.modules.event;

import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class FollowEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleFollowEvent(FollowEvent event) {

        notificationRepository.save(Notification.builder()
                .account(event.getFollowingTarget())
                .sender(event.getFollower())
                .message("님이 회원님을 팔로우하기 시작했습니다.")
                .checked(false)
                .notificationType(NotificationType.FOLLOW)
                .build());
    }
}
