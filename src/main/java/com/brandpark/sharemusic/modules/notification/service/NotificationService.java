package com.brandpark.sharemusic.modules.notification.service;

import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void checkNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();

        notification.check();
    }

    public int getNotReadCount(Long accountId, NotificationType type) {
        if (type == null) {
            return notificationRepository.countByAccountIdAndCheckedIsFalse(accountId);
        }

        return notificationRepository.countByAccountIdAndNotificationTypeAndCheckedIsFalse(accountId, type);
    }
}
