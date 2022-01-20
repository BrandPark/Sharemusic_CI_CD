package com.brandpark.sharemusic.modules.notification.service;

import com.brandpark.sharemusic.infra.config.auth.CustomUserDetails;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
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
    private final AccountRepository accountRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler
            , ModelAndView modelAndView) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (modelAndView != null && !isRedirectView(modelAndView)
                && authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {

            SessionAccount account = ((CustomUserDetails) authentication.getPrincipal()).getSessionAccount();


            List<NotificationForm> notifications = notificationRepository.findFirst10ByAccountIdOrderByCheckedAscCreatedDateDesc(account.getId())
                    .stream()
                    .map(no -> {
                        Account sender = accountRepository.findById(no.getSenderId())
                                .orElseThrow(() -> new IllegalArgumentException("알림을 보낸 계정이 존재하지 않습니다."));

                        return new NotificationForm(
                                no.getId()
                                , sender.getProfileImage()
                                , sender.getNickname()
                                , no.getMessage()
                                , no.getLink()
                                , no.isChecked()
                                , no.getCreatedDate()
                                , no.getNotificationType().name());
                    })
                    .collect(Collectors.toList());

            int notReadCount = notificationRepository.countByAccountIdAndCheckedIsFalse(account.getId());

            modelAndView.addObject("hasNotification", notifications.size() > 0);
            modelAndView.addObject("notReadCount", notReadCount);
            modelAndView.addObject("notifications", notifications);
        }
    }

    private boolean isRedirectView(ModelAndView mv) {
        return mv.getView() instanceof RedirectView || mv.getViewName().startsWith("redirect:");
    }
}
