package com.brandpark.sharemusic.modules.notification.domain;

import java.util.List;

public interface ExtendNotificationRepository {
    int batchInsert(List<Notification> notifications);
}
