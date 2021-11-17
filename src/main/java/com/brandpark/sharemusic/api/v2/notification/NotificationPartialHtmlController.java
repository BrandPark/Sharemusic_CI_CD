package com.brandpark.sharemusic.api.v2.notification;

import com.brandpark.sharemusic.api.v1.notification.query.NotificationQueryRepository;
import com.brandpark.sharemusic.api.v1.notification.query.dto.NotificationInfo;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PageHtmlResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.notification.NotificationService;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NotificationPartialHtmlController {

    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationService notificationService;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/api/v2/notifications")
    public PageHtmlResult getNotificationListHtml(@LoginAccount SessionAccount account
            , @PageableDefault(size = 10) Pageable pageable
            , @RequestParam(name = "type") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        int notReadCount = notificationService.getNotReadCount(account.getId(), notificationType);

        PagingDto<NotificationInfo> page = notificationQueryRepository.findAllNotifications(pageable, notificationType, account.getId());
        List<NotificationInfo> notifications = page.getContents();

        Context context = new Context();
        context.setVariable("hasNotification", notifications.size() > 0);
        context.setVariable("notReadCount", notReadCount);
        context.setVariable("notifications", notifications);

        String notificationsHtml = htmlCreator.getListHtml("partial/notifications", context);

        String paginationHtml = htmlCreator.getPaginationHtml(page);

        return new PageHtmlResult(notificationsHtml, paginationHtml);
    }
}
