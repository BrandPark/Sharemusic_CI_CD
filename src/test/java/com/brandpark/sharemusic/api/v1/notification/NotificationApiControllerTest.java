package com.brandpark.sharemusic.api.v1.notification;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.api.v1.notification.query.dto.NotificationInfo;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.notification.NotificationType;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
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
        myAccount = accountFactory.createAccount("내 계정");
        otherAccount = accountFactory.createAccount("다른 사람 계정");
        accountRepository.saveAll(List.of(myAccount, otherAccount));
    }

    @DisplayName("확인한 알림은 체크된다. - 실패(로그인하지 않은 상태)")
    @Test
    void CheckNotification_Fail_When_NotAuthenticated() throws Exception {

        // given
        Notification myNotification
                = notificationFactory.persistNotification(otherAccount, myAccount, NotificationType.FOLLOW);

        // when
        mockMvc.perform(put("/api/v1/notifications/" + myNotification.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("확인한 알림은 체크된다. - 실패(다른 사람의 알림을 변경하려는 경우)")
    @Test
    void CheckNotification_Fail_When_OtherAccountNotification() throws Exception {

        // given
        Notification otherAccountNotification
                = notificationFactory.persistNotification(myAccount, otherAccount, NotificationType.FOLLOW);

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
    @DisplayName("확인한 알림은 체크된다. - 실패(잘못된 notification_id)")
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
    @DisplayName("확인한 알림은 체크된다. - 성공")
    @Test
    void CheckNotification_Success() throws Exception {

        // given
        Notification myNotification = notificationFactory.persistNotification(otherAccount, myAccount, NotificationType.FOLLOW);

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

    @DisplayName("알림 리스트 페이지 조회 - 실패(로그인 하지 않은 경우)")
    @Test
    public void RetrieveNotificationListPage_Fail_When_NotAuthenticated() throws Exception {

        // given
        int notificationCount = 20;
        List<Notification> myNotificationList
                = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.FOLLOW, notificationCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", NotificationType.FOLLOW.name()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모든 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void RetrieveNotificationListPage_Success_When_AllType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", "all"))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    var resultPage = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8)
                            , new TypeReference<PagingDto<NotificationInfo>>() {
                            });

                    assertThat(resultPage.getTotalElements()).isEqualTo(totalCount);

                    NotificationInfo resultInfo = resultPage.getContents().get(0);
                    Notification expected = followTypes.get(0);
                    assertThat(resultInfo.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultInfo.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultInfo.getNotificationType()).isNotNull();
                    assertThat(resultInfo.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultInfo.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("FOLLOW 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void RetrieveNotificationListPage_Success_When_FollowType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", NotificationType.FOLLOW.name()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    var resultPage = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8)
                            , new TypeReference<PagingDto<NotificationInfo>>() {
                            });

                    assertThat(resultPage.getTotalElements()).isEqualTo(followTypeCount);

                    NotificationInfo resultInfo = resultPage.getContents().get(0);
                    Notification expected = followTypes.get(0);
                    assertThat(resultInfo.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultInfo.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultInfo.getNotificationType()).isEqualTo(NotificationType.FOLLOW);
                    assertThat(resultInfo.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultInfo.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("COMMENT 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void RetrieveNotificationListPage_Success_When_CommentType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", NotificationType.COMMENT.name()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    var resultPage = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8)
                            , new TypeReference<PagingDto<NotificationInfo>>() {
                            });

                    assertThat(resultPage.getTotalElements()).isEqualTo(commentTypeCount);

                    NotificationInfo resultInfo = resultPage.getContents().get(0);
                    Notification expected = commentTypes.get(0);
                    assertThat(resultInfo.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultInfo.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultInfo.getNotificationType()).isEqualTo(NotificationType.COMMENT);
                    assertThat(resultInfo.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultInfo.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("CREATED_ALBUM_BY_FOLLOWER 타입 알림 리스트 페이지 조회 - 성공")
    @Test
    public void RetrieveNotificationListPage_Success_When_CreatedAlbumType() throws Exception {

        // given
        int followTypeCount = 5;
        int commentTypeCount = 8;
        int createAlbumTypeCount = 10;
        int totalCount = followTypeCount + commentTypeCount + createAlbumTypeCount;

        List<Notification> followTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.FOLLOW, followTypeCount);
        List<Notification> commentTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.COMMENT, commentTypeCount);
        List<Notification> createAlbumTypes = notificationFactory.persistNotificationList(otherAccount, myAccount, NotificationType.CREATED_ALBUM_BY_FOLLOWER, createAlbumTypeCount);

        // when
        mockMvc.perform(get("/api/v1/notifications")
                        .param("page", "0")
                        .param("type", NotificationType.CREATED_ALBUM_BY_FOLLOWER.name()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    var resultPage = objectMapper.readValue(result.getResponse().getContentAsString(UTF_8)
                            , new TypeReference<PagingDto<NotificationInfo>>() {
                            });

                    assertThat(resultPage.getTotalElements()).isEqualTo(createAlbumTypeCount);

                    NotificationInfo resultInfo = resultPage.getContents().get(0);
                    Notification expected = createAlbumTypes.get(0);
                    assertThat(resultInfo.getMessage()).isEqualTo(expected.getMessage());
                    assertThat(resultInfo.getLink()).isEqualTo(expected.getLink());
                    assertThat(resultInfo.getNotificationType()).isEqualTo(NotificationType.CREATED_ALBUM_BY_FOLLOWER);
                    assertThat(resultInfo.getSenderNickname()).isEqualTo(expected.getSender().getNickname());
                    assertThat(resultInfo.getSenderProfileImage()).isEqualTo(expected.getSender().getProfileImage());
                });
    }
}