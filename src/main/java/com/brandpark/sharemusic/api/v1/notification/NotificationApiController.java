package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.notification.query.NotificationQueryRepository;
import com.brandpark.sharemusic.api.v1.notification.dto.NotificationInfoResponse;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.api.v1.Validator;
import com.brandpark.sharemusic.modules.notification.NotificationService;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class NotificationApiController {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationService notificationService;
    private final Validator validator;

    @GetMapping("/notifications")
    public PageResult<NotificationInfoResponse> getNotificationListPage(@LoginAccount SessionAccount account
            , @PageableDefault Pageable pageable
            , @RequestParam(name = "type", defaultValue = "") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        return notificationQueryRepository.findAllNotifications(pageable, notificationType, account.getId());
    }

    @PutMapping("/notifications/{notificationId}")
    public Long readCheckNotification(@LoginAccount SessionAccount account, @PathVariable Long notificationId) {

        validator.validateReadCheckNotification(account, notificationId);

        notificationService.checkNotification(notificationId);

        return notificationId;
    }

    @PutMapping("/notifications")
    public int readCheckAllNotifications(@LoginAccount SessionAccount account
            ,@RequestParam("type") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        if (notificationType == null) {
            return notificationRepository.checkAllNotification(account.getId());
        }

        return notificationRepository.checkAllNotification(account.getId(), notificationType);
    }
}
