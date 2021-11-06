package com.brandpark.sharemusic.modules.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByAccountIdOrderByCreatedDateDesc(Long accountId);
}
