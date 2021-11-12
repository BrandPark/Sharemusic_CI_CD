package com.brandpark.sharemusic.modules.notification;

import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void checkNotification(Notification notification) {
        notification.check();
    }

    public NotificationType getNotificationType(String typeName) {
        boolean isContain = Arrays.stream(NotificationType.values())
                .map(nt -> nt.name())
                .collect(Collectors.toList()).contains(typeName.toUpperCase());

        if (!isContain) {
            return null;
        }

        return NotificationType.valueOf(typeName.toUpperCase());
    }

    public int getNotReadCount(Long accountId, NotificationType type) {
        if (type == null) {
            return notificationRepository.countByAccountIdAndCheckedIsFalse(accountId);
        }
        return notificationRepository.countByAccountIdAndNotificationTypeAndCheckedIsFalse(accountId, type);
    }
}
