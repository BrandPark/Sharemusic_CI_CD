package com.brandpark.sharemusic.partials.notification;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.notification.service.NotificationService;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.partials.PageHtmlResult;
import com.brandpark.sharemusic.partials.PagingHtmlCreator;
import com.brandpark.sharemusic.partials.notification.form.NotificationInfoForm;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
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

    private final NotificationPartialRepository notificationPartialRepository;
    private final NotificationService notificationService;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/partial/notifications")
    public PageHtmlResult getNotificationListHtml(@LoginAccount SessionAccount account
            , @PageableDefault Pageable pageable, @RequestParam(name = "type") String type) {

        NotificationType notificationType = NotificationType.getTypeByName(type);

        PagingDto<NotificationInfoForm> page = notificationPartialRepository.findAllNotifications(pageable, notificationType, account.getId());
        List<NotificationInfoForm> notifications = page.getContents();

        Context context = new Context();
        context.setVariable("hasNotification", notifications.size() > 0);
        context.setVariable("notReadCount", notificationService.getNotReadCount(account.getId(), notificationType));

        return htmlCreator.getPageHtmlResult(context, page, "notifications", "partial/notifications");
    }
}
