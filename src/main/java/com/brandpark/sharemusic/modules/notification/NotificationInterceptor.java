package com.brandpark.sharemusic.modules.notification;

import com.brandpark.sharemusic.infra.config.auth.CustomUserDetails;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import com.brandpark.sharemusic.modules.notification.form.NotificationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler
            , ModelAndView modelAndView) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (modelAndView != null && !isRedirectView(modelAndView)
                && authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {

            SessionAccount account = ((CustomUserDetails) authentication.getPrincipal()).getSessionAccount();

            List<NotificationForm> notifications = notificationRepository.findAllByAccountIdOrderByCreatedDateDesc(account.getId())
                    .stream()
                    .map(no -> new NotificationForm(
                            no.getId()
                            , no.getSender().getProfileImage()
                            , no.getSender().getNickname()
                            , no.getMessage()
                            , no.getLink()
                            , no.isChecked()
                            , no.getCreatedDate()
                            , no.getNotificationType().name()))
                    .collect(Collectors.toList());

            modelAndView.addObject("hasNotification", notifications.size() > 0);
            modelAndView.addObject("notifications", notifications);
        }
    }

    private boolean isRedirectView(ModelAndView mv) {
        return mv.getView() instanceof RedirectView || mv.getViewName().startsWith("redirect:");
    }
}
