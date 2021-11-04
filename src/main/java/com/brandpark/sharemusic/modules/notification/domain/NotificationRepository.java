package com.brandpark.sharemusic.modules.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    int countByAccountIdAndChecked(Long accountId, boolean checked);
}
