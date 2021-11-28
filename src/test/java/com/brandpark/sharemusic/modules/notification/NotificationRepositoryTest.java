package com.brandpark.sharemusic.modules.notification;

import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.NotificationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static com.brandpark.sharemusic.modules.notification.NotificationType.COMMENT;
import static com.brandpark.sharemusic.modules.notification.NotificationType.FOLLOW;
import static org.assertj.core.api.Assertions.assertThat;

@MockMvcTest
public class NotificationRepositoryTest {

    @Autowired NotificationRepository notificationRepository;
    @Autowired NotificationFactory notificationFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired EntityManager entityManager;
    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("user");
        otherAccount = accountFactory.persistAccount("otherAccount");
    }

    @DisplayName("읽음 체크안된 것과 가장 최근 알림 순서로 10개 조회")
    @Test
    public void FindAllFirst10ByAccountIdOrderByCheckedAscCreatedDateDesc() throws Exception {

        // given
        int totalNotificationCount = 12;
        notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, totalNotificationCount);

        // when
        List<Notification> result = notificationRepository.findFirst10ByAccountIdOrderByCheckedAscCreatedDateDesc(myAccount.getId());

        // then
        assertThat(result.size()).isEqualTo(10);

        Notification resultOne = result.get(0);
        AssertUtil.assertEntityIsNotEmpty(resultOne);

        assertThat(resultOne.getAccount().getId()).isEqualTo(myAccount.getId());
        assertThat(resultOne.getCreatedDate()).isAfterOrEqualTo(result.get(9).getCreatedDate());
    }

    @DisplayName("읽지 않은 알림 개수 확인")
    @Test
    public void CountByAccountIdAndCheckedIsFalse() throws Exception {

        // given
        int totalNotificationCount = 12;
        List<Notification> notifications = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, totalNotificationCount);

        int readCount = 5;
        for (int i = 0; i < readCount; i++) {
            notifications.get(i).check();
        }

        // when
        int notReadCount = notificationRepository.countByAccountIdAndCheckedIsFalse(myAccount.getId());

        // then
        assertThat(notReadCount).isEqualTo(totalNotificationCount - readCount);
    }

    @DisplayName("Follow 타입의 읽지 않은 알림 개수 확인")
    @Test
    public void CountByAccountIdAndNotificationTypeAndCheckedIsFalse() throws Exception {

        // given
        int followTypeCount = 3;
        List<Notification> notReadFollowTypeList = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, followTypeCount);

        int commentTypeCount = 2;
        List<Notification> notReadCommentTypeList = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, commentTypeCount);

        // when
        int result = notificationRepository.countByAccountIdAndNotificationTypeAndCheckedIsFalse(myAccount.getId(), FOLLOW);

        // then
        assertThat(result).isEqualTo(notReadFollowTypeList.size());
    }

    @DisplayName("한 계정의 모든 타입 알림 읽음 표시")
    @Test
    public void CheckAllNotification() throws Exception {

        // given
        int notReadCount = 15;
        notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, notReadCount);

        // when
        notificationRepository.checkAllNotification(myAccount.getId());

        // then
        List<Notification> all = notificationRepository.findAll();
        assertThat(all.size()).isEqualTo(notReadCount);

        for (Notification notification : all) {
            assertThat(notification.isChecked()).isTrue();
        }
    }

    @DisplayName("한 계정의 팔로우 타입 알림 읽음 표시")
    @Test
    public void CheckAllNotification_FollowType() throws Exception {

        // given
        int notReadFollowTypeCount = 15;
        notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, notReadFollowTypeCount);

        int notReadCommentTypeCount = 2;
        notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, notReadCommentTypeCount);

        // when
        notificationRepository.checkAllNotification(myAccount.getId(), FOLLOW);

        // then
        List<Notification> all = notificationRepository.findAll();
        assertThat(all.size()).isEqualTo(notReadCommentTypeCount + notReadFollowTypeCount);

        for (Notification notification : all) {
            if (notification.getNotificationType() == FOLLOW) {
                assertThat(notification.isChecked()).isTrue();
            } else {
                assertThat(notification.isChecked()).isFalse();
            }
        }
    }
}
