package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@MockMvcTest
class NotificationApiControllerTest {

    @Autowired AccountFactory accountFactory;
    @Autowired NotificationRepository notificationRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired MockMvc mockMvc;
    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("내 계정");
        otherAccount = accountFactory.createAccount("다른 사람 계정");
        accountRepository.saveAll(List.of(myAccount, otherAccount));
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("확인한 알림은 체크된다.")
    @Test
    void CheckNotification() {

        // given
        Notification savedNotification = notificationRepository.save(Notification.builder()
                .link("link")
                .notificationType(NotificationType.FOLLOW)
                .checked(false)
                .sender(otherAccount)
                .account(myAccount)
                .message("다른 사람이 내 계정을 팔로우 했습니다.")
                .build());

        // when


        // then
    }

    @Test
    void getNotificationListHtml() {
    }
}