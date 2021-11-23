package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.notification.query.NotificationQueryRepository;
import com.brandpark.sharemusic.api.v1.notification.query.dto.NotificationInfo;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.notification.NotificationService;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
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
    private final DtoValidator validator;

    @PutMapping("/notifications/{notificationId}")
    public Long checkNotification(@LoginAccount SessionAccount account, @PathVariable Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION));

        validator.validateNotification(account.getId(), notification.getAccount().getId());

        notificationService.checkNotification(notification);

        return notificationId;
    }

    @GetMapping("/notifications")
    public PagingDto<NotificationInfo> getNotificationListPage(@LoginAccount SessionAccount account
            , @PageableDefault(size = 10) Pageable pageable
            , @RequestParam(name = "type", defaultValue = "") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        return notificationQueryRepository.findAllNotifications(pageable, notificationType, account.getId());
    }

    @PutMapping("/notifications")
    public int checkAllNotifications(@LoginAccount SessionAccount account
            ,@RequestParam("type") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        if (notificationType == null) {
            return notificationRepository.checkAllNotification(account.getId());
        }

        return notificationRepository.checkAllNotification(account.getId(), notificationType);
    }

}
