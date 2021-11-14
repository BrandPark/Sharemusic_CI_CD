package com.brandpark.sharemusic.api.v1.notification.query;

import com.brandpark.sharemusic.api.v1.notification.query.dto.NotificationInfo;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.testUtils.AccountFactory;
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
import static org.assertj.core.api.Assertions.assertThat;

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
        myAccount = accountFactory.createAccount("내 계정");
        otherAccount = accountFactory.createAccount("다른 사람 계정");

        accountRepository.saveAll(List.of(myAccount, otherAccount));
    }

    @DisplayName("모든 알림 페이징 조회 - 모든 타입")
    @Test
    public void RetrieveAllNotificationsByPage_AllType() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int allNotificationCount = followTypes.size() + commentTypes.size() + createdAlbumTypes.size();

        // when
        PagingDto<NotificationInfo> result = notificationQueryRepository.findAllNotifications(pageRequest, null, myAccount.getId());

        // then
        assertThat(result.getPageSize()).isEqualTo(pageRequest.getPageSize());
        assertThat(result.getPageNumber()).isEqualTo(pageRequest.getPageNumber());

        assertThat(result.getTotalElements()).isEqualTo(allNotificationCount);
    }

    @DisplayName("모든 알림 페이징 조회 - FollowType")
    @Test
    public void RetrieveAllNotificationsByPage_FollowType() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int followTypeCount = followTypes.size();

        // when
        PagingDto<NotificationInfo> result = notificationQueryRepository.findAllNotifications(pageRequest, FOLLOW, myAccount.getId());

        // then
        assertThat(result.getPageSize()).isEqualTo(pageRequest.getPageSize());
        assertThat(result.getPageNumber()).isEqualTo(pageRequest.getPageNumber());

        assertThat(result.getTotalElements()).isEqualTo(followTypeCount);
    }

    @DisplayName("모든 알림 페이징 조회 - CommentType")
    @Test
    public void RetrieveAllNotificationsByPage_CommentType() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int commentTypeCount = commentTypes.size();

        // when
        PagingDto<NotificationInfo> result = notificationQueryRepository.findAllNotifications(pageRequest, COMMENT, myAccount.getId());

        // then
        assertThat(result.getPageSize()).isEqualTo(pageRequest.getPageSize());
        assertThat(result.getPageNumber()).isEqualTo(pageRequest.getPageNumber());

        assertThat(result.getTotalElements()).isEqualTo(commentTypeCount);
    }

    @DisplayName("모든 알림 페이징 조회 - CreatedAlbumByFollowerType")
    @Test
    public void RetrieveAllNotificationsByPage_CreatedAlbum() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, FOLLOW, 5);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, COMMENT, 6);
        List<Notification> createdAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, 7);
        int createdAlbumTypeCount = createdAlbumTypes.size();

        // when
        PagingDto<NotificationInfo> result = notificationQueryRepository.findAllNotifications(pageRequest, CREATED_ALBUM_BY_FOLLOWER, myAccount.getId());

        // then
        assertThat(result.getPageSize()).isEqualTo(pageRequest.getPageSize());
        assertThat(result.getPageNumber()).isEqualTo(pageRequest.getPageNumber());

        assertThat(result.getTotalElements()).isEqualTo(createdAlbumTypeCount);
    }
}