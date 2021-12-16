package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.api.v1.notification.dto.NotificationInfoResponse;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.NotificationFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.List;

import static com.brandpark.sharemusic.api.v1.exception.Error.FORBIDDEN_EXCEPTION;
import static com.brandpark.sharemusic.api.v1.exception.Error.ILLEGAL_ARGUMENT_EXCEPTION;
import static com.brandpark.sharemusic.modules.notification.NotificationType.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class NotificationApiControllerTest {

    @Autowired AccountFactory accountFactory;
    @Autowired NotificationRepository notificationRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager entityManager;
    @Autowired ObjectMapper objectMapper;
    @Autowired NotificationFactory notificationFactory;
    @Autowired MockMvc mockMvc;
    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("내 계정");
        otherAccount = accountFactory.persistAccount("다른 사람 계정");
    }

    @DisplayName("알림 리스트 페이지 조회 - 실패(로그인 하지 않은 경우)")
    @Test
    public void FindNotificationListPage_Fail_When_NotAuthenticated() throws Exception {

        // given
        int notificationCount = 20;
        List<Notification> myNotificationList
                = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, notificationCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", FOLLOW.name()))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void FindNotificationListPage_Success_When_AllType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        int pageNum = 0;
        int pageSize = 10;

        String url = "/api/v1/notifications";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", "all"))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);

                    PageResult<NotificationInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, totalCount, resultPage);

                    NotificationInfoResponse resultOne = resultPage.getContent().get(0);

                    AssertUtil.assertDtoIsNotEmpty(resultOne);

                    Notification expected = followTypes.get(0);
                    assertThat(resultOne.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultOne.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultOne.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultOne.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("FOLLOW 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void FindNotificationListPage_Success_When_FollowType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        int pageNum = 0;
        int pageSize = 10;

        String url = "/api/v1/notifications";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", FOLLOW.name()))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);

                    PageResult<NotificationInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, followTypeCount, resultPage);

                    NotificationInfoResponse resultOne = resultPage.getContent().get(0);

                    AssertUtil.assertDtoIsNotEmpty(resultOne);

                    Notification expected = followTypes.get(0);
                    assertThat(resultOne.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultOne.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultOne.getNotificationType()).isEqualTo(FOLLOW);
                    assertThat(resultOne.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultOne.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("COMMENT 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void FindNotificationListPage_Success_When_CommentType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        int pageNum = 0;
        int pageSize = 10;

        String url = "/api/v1/notifications";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", COMMENT.name()))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);

                    PageResult<NotificationInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, commentTypeCount, resultPage);

                    NotificationInfoResponse resultOne = resultPage.getContent().get(0);

                    AssertUtil.assertDtoIsNotEmpty(resultOne);

                    Notification expected = commentTypes.get(0);
                    assertThat(resultOne.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultOne.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultOne.getNotificationType()).isEqualTo(COMMENT);
                    assertThat(resultOne.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultOne.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("CREATED_ALBUM_BY_FOLLOWER 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void FindNotificationListPage_Success_When_CreatedAlbumType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotReadNotificationList(otherAccount, myAccount, CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        int pageNum = 0;
        int pageSize = 10;

        String url = "/api/v1/notifications";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize))
                        .param("type", CREATED_ALBUM_BY_FOLLOWER.name()))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(UTF_8);

                    PageResult<NotificationInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, createAlbumTypeCount, resultPage);

                    NotificationInfoResponse resultOne = resultPage.getContent().get(0);

                    AssertUtil.assertDtoIsNotEmpty(resultOne);

                    Notification expected = createAlbumTypes.get(0);
                    assertThat(resultOne.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultOne.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultOne.getNotificationType()).isEqualTo(CREATED_ALBUM_BY_FOLLOWER);
                    assertThat(resultOne.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultOne.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @DisplayName("알림을 읽음 표시한다. - 실패(로그인하지 않은 상태)")
    @Test
    void CheckNotification_Fail_When_NotAuthenticated() throws Exception {

        // given
        Notification myNotification
                = notificationFactory.persistNotification(otherAccount, myAccount, FOLLOW);

        // when
        mockMvc.perform(put("/api/v1/notifications/" + myNotification.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알림을 읽음 표시한다. - 실패(다른 사람의 알림을 변경하려는 경우)")
    @Test
    void CheckNotification_Fail_When_OtherAccountNotification() throws Exception {

        // given
        Notification otherAccountNotification
                = notificationFactory.persistNotification(myAccount, otherAccount, FOLLOW);

        // when
        mockMvc.perform(put("/api/v1/notifications/" + otherAccountNotification.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알림을 읽음 표시한다. - 실패(잘못된 notification_id)")
    @Test
    void CheckNotification_Fail_When_InputWrongNotificationId() throws Exception {

        // given
        Long wrongNotificationId = 9999999L;

        // when
        mockMvc.perform(put("/api/v1/notifications/" + wrongNotificationId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult
                            = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8), ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알림을 읽음 표시한다. - 성공")
    @Test
    void CheckNotification_Success() throws Exception {

        // given
        Notification myNotification = notificationFactory.persistNotification(otherAccount, myAccount, FOLLOW);

        assertThat(myNotification.isChecked()).isFalse();

        // when
        mockMvc.perform(put("/api/v1/notifications/" + myNotification.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        // then
        entityManager.flush();
        entityManager.clear();
        Notification checkedNotification = notificationRepository.findById(myNotification.getId()).get();

        assertThat(checkedNotification.isChecked()).isTrue();
    }

    @DisplayName("모든 타입 알림 읽음 표시 - 실패(로그인 하지 않은 경우)")
    @Test
    public void AllNotificationCheck_Fail_When_NotAuthenticated() throws Exception {

        // given
        NotificationType[] types = {FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER};
        List<Notification> notifications = notificationFactory.persistNotReadNotificationListMultiType(otherAccount, myAccount, types);
        for (Notification notification : notifications) {
            assertThat(notification.isChecked()).isFalse();
        }

        // when
        String notificationType = "all";
        mockMvc.perform(put("/api/v1/notifications")
                        .with(csrf())
                        .param("type", notificationType))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 알림 읽음 표시 - 성공(FOLLOW 타입)")
    @Test
    public void AllNotificationCheck_Success_FollowType() throws Exception {

        // given
        NotificationType[] types = {FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER};
        List<Notification> notifications = notificationFactory.persistNotReadNotificationListMultiType(otherAccount, myAccount, types);
        for (Notification notification : notifications) {
            assertThat(notification.isChecked()).isFalse();
        }

        // when
        String notificationType = FOLLOW.name();
        mockMvc.perform(put("/api/v1/notifications")
                        .with(csrf())
                        .param("type", notificationType))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    int updatedCount = Integer.parseInt(result.getResponse().getContentAsString(UTF_8));
                    assertThat(updatedCount).isEqualTo(1);
                });

        // then
        List<Notification> updatedNotifications = notificationRepository.findAll();
        for (Notification notification : updatedNotifications) {
            if (notification.getNotificationType() == FOLLOW) {
                assertThat(notification.isChecked()).isTrue();
            } else {
                assertThat(notification.isChecked()).isFalse();
            }
        }
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 알림 읽음 표시 - 성공(COMMENT 타입)")
    @Test
    public void AllNotificationCheck_Success_CommentType() throws Exception {

        // given
        NotificationType[] types = {FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER};
        List<Notification> notifications = notificationFactory.persistNotReadNotificationListMultiType(otherAccount, myAccount, types);
        for (Notification notification : notifications) {
            assertThat(notification.isChecked()).isFalse();
        }

        // when
        String notificationType = COMMENT.name();
        mockMvc.perform(put("/api/v1/notifications")
                        .with(csrf())
                        .param("type", notificationType))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    int updatedCount = Integer.parseInt(result.getResponse().getContentAsString(UTF_8));
                    assertThat(updatedCount).isEqualTo(1);
                });

        // then
        List<Notification> updatedNotifications = notificationRepository.findAll();
        for (Notification notification : updatedNotifications) {
            if (notification.getNotificationType() == COMMENT) {
                assertThat(notification.isChecked()).isTrue();
            } else {
                assertThat(notification.isChecked()).isFalse();
            }
        }
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 알림 읽음 표시 - 성공(CREATED_ALBUM_BY_FOLLOWER 타입)")
    @Test
    public void AllNotificationCheck_Success_CreatedAlbum() throws Exception {

        // given
        NotificationType[] types = {FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER};
        List<Notification> notifications = notificationFactory.persistNotReadNotificationListMultiType(otherAccount, myAccount, types);
        for (Notification notification : notifications) {
            assertThat(notification.isChecked()).isFalse();
        }

        // when
        String notificationType = CREATED_ALBUM_BY_FOLLOWER.name();
        mockMvc.perform(put("/api/v1/notifications")
                        .with(csrf())
                        .param("type", notificationType))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    int updatedCount = Integer.parseInt(result.getResponse().getContentAsString(UTF_8));
                    assertThat(updatedCount).isEqualTo(1);
                });

        // then
        List<Notification> updatedNotifications = notificationRepository.findAll();
        for (Notification notification : updatedNotifications) {
            if (notification.getNotificationType() == CREATED_ALBUM_BY_FOLLOWER) {
                assertThat(notification.isChecked()).isTrue();
            } else {
                assertThat(notification.isChecked()).isFalse();
            }
        }
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 타입 알림 읽음 표시 - 성공(모든 타입)")
    @Test
    public void AllNotificationCheck_Success() throws Exception {

        // given
        NotificationType[] types = {FOLLOW, COMMENT, CREATED_ALBUM_BY_FOLLOWER};
        List<Notification> notifications = notificationFactory.persistNotReadNotificationListMultiType(otherAccount, myAccount, types);
        for (Notification notification : notifications) {
            assertThat(notification.isChecked()).isFalse();
        }

        // when
        String notificationType = "all";
        mockMvc.perform(put("/api/v1/notifications")
                        .with(csrf())
                        .param("type", notificationType))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    int updatedCount = Integer.parseInt(result.getResponse().getContentAsString(UTF_8));
                    assertThat(updatedCount).isEqualTo(notifications.size());
                });

        // then
        List<Notification> updatedNotifications = notificationRepository.findAll();
        for (Notification notification : updatedNotifications) {
            assertThat(notification.isChecked()).isTrue();
        }
    }
}