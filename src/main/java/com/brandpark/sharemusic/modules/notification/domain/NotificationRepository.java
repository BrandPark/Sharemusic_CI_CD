package com.brandpark.sharemusic.modules.notification.domain;

import com.brandpark.sharemusic.modules.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long>, ExtendNotificationRepository {

    List<Notification> findFirst10ByAccountIdOrderByCheckedAscCreatedDateDesc(Long accountId);

    int countByAccountIdAndCheckedIsFalse(Long accountId);

    int countByAccountIdAndNotificationTypeAndCheckedIsFalse(Long accountId, NotificationType type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.checked = true WHERE n.checked = false AND n.account.id = :accountId AND n.notificationType = :type")
    int checkAllNotification(@Param("accountId") Long accountId, @Param("type") NotificationType type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Notification n SET n.checked = true WHERE n.checked = false AND n.account.id = :accountId")
    int checkAllNotification(@Param("accountId") Long accountId);

    @Override
    int batchInsert(List<Notification> notifications);
}
