package com.brandpark.sharemusic.modules.notification;

import com.brandpark.sharemusic.modules.notification.domain.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class NotificationService {

    @Transactional
    public void checkNotification(Notification notification) {
        notification.check();
    }
}
