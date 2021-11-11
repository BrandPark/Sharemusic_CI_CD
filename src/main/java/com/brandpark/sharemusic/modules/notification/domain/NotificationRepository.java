package com.brandpark.sharemusic.modules.notification.domain;

import com.brandpark.sharemusic.modules.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllFirst10ByAccountIdOrderByCreatedDateDesc(Long accountId);

    int countByAccountIdAndCheckedIsFalse(Long accountId);

    int countByAccountIdAndNotificationTypeAndCheckedIsFalse(Long accountId, NotificationType type);
}
