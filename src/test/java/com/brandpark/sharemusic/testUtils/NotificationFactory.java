package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ActiveProfiles("Test")
@Component
public class NotificationFactory {

    private final NotificationRepository notificationRepository;

    public Notification persistNotification(Account sender, Account receiver, NotificationType type) {
        return notificationRepository.save(createNotification(sender, receiver, type));
    }

    public List<Notification> persistNotificationListMultiType(Account sender, Account receiver, NotificationType... types) {
        return notificationRepository.saveAll(createNotificationListMultiType(sender, receiver, types));
    }

    public List<Notification> persistNotificationList(Account sender, Account receiver, NotificationType type, int count) {
        return notificationRepository.saveAll(createNotificationList(sender, receiver, type, count));
    }

    private Notification createNotification(Account sender, Account receiver, NotificationType type) {
        return Notification.builder()
                .link("link")
                .notificationType(type)
                .checked(false)
                .sender(sender)
                .account(receiver)
                .message("message")
                .build();
    }

    private List<Notification> createNotificationListMultiType(Account sender, Account receiver, NotificationType...types) {

        return Arrays.stream(types)
                .map(type -> createNotification(sender, receiver, type))
                .collect(Collectors.toList());
    }

    private List<Notification> createNotificationList(Account sender, Account receiver, NotificationType type, int count) {

        List<Notification> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            result.add(createNotification(sender, receiver, type));
        }

        return result;
    }
}
