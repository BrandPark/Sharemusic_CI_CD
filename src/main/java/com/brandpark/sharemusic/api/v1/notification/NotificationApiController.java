package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.notification.NotificationService;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationApiController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final DtoValidator validator;

    @PutMapping("/api/v1/notifications/{notificationId}")
    public void checkNotification(@LoginAccount SessionAccount account, @PathVariable Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION));

        validator.validateNotification(account.getId(), notification.getAccount().getId());

        notificationService.checkNotification(notification);
    }
}
