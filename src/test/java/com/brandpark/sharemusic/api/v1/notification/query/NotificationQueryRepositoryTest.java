package com.brandpark.sharemusic.api.v1.notification.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.notification.dto.NotificationInfoResponse;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.NotificationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.brandpark.sharemusic.modules.notification.NotificationType.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class NotificationQueryRepositoryTest {

    @Autowired NotificationQueryRepository notificationQueryRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired NotificationFactory notificationFactory;
    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("내 계정");
        otherAccount = accountFactory.persistAccount("다른 사람 계정");
    }

    @DisplayName("모든 알림 페이징 조회 - 모든 타입")
    @Test
    public void RetrieveAllNotificationsByPage_AllType() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int allNotificationCount = followTypes.size() + commentTypes.size() + createdAlbumTypes.size();

        // when
        PageResult<NotificationInfoResponse> result = notificationQueryRepository.findAllNotifications(pageRequest, null, myAccount.getId());

        // then
        AssertUtil.assertPageResult(pageNum, pageSize, allNotificationCount, result);

        NotificationInfoResponse resultOne = result.getContent().get(0);
        AssertUtil.assertDtoIsNotEmpty(resultOne);
    }

    @DisplayName("모든 알림 페이징 조회 - FollowType")
    @Test
    public void RetrieveAllNotificationsByPage_FollowType() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int followTypeCount = followTypes.size();

        // when
        PageResult<NotificationInfoResponse> result = notificationQueryRepository.findAllNotifications(pageRequest, FOLLOW, myAccount.getId());

        // then
        AssertUtil.assertPageResult(pageNum, pageSize, followTypeCount, result);

        NotificationInfoResponse resultOne = result.getContent().get(0);
        AssertUtil.assertDtoIsNotEmpty(resultOne);
    }

    @DisplayName("모든 알림 페이징 조회 - CommentType")
    @Test
    public void RetrieveAllNotificationsByPage_CommentType() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int commentTypeCount = commentTypes.size();

        // when
        PageResult<NotificationInfoResponse> result = notificationQueryRepository.findAllNotifications(pageRequest, COMMENT, myAccount.getId());

        // then
        AssertUtil.assertPageResult(pageNum, pageSize, commentTypeCount, result);

        NotificationInfoResponse resultOne = result.getContent().get(0);
        AssertUtil.assertDtoIsNotEmpty(resultOne);
    }

    @DisplayName("모든 알림 페이징 조회 - CreatedAlbumByFollowerType")
    @Test
    public void RetrieveAllNotificationsByPage_CreatedAlbum() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int createdAlbumTypeCount = createdAlbumTypes.size();

        // when
        PageResult<NotificationInfoResponse> result = notificationQueryRepository.findAllNotifications(pageRequest, CREATED_ALBUM_BY_FOLLOWER, myAccount.getId());

        // then
        AssertUtil.assertPageResult(pageNum, pageSize, createdAlbumTypeCount, result);

        NotificationInfoResponse resultOne = result.getContent().get(0);
        AssertUtil.assertDtoIsNotEmpty(resultOne);
    }
}