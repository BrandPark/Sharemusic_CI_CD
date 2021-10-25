package com.brandpark.sharemusic.api.v1.follow;

import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.follow.Follow;
import com.brandpark.sharemusic.modules.follow.FollowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class FollowApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired FollowRepository followRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired ObjectMapper objectMapper;
    Account otherAccount;
    Account myAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("내 계정");
        otherAccount = accountFactory.createAccount("다른 사람 계정");
        accountRepository.saveAll(List.of(myAccount, otherAccount));
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 실패(url의 targetId가 잘못된 경우)")
    @Test
    public void Follow_Fail_When_WrongTargetId() throws Exception {

        // given
        // when, then
        Long wrongId = 999999L;
        String url = "/api/v1/accounts/" + wrongId + "/follow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로우 - 실패(로그인하지 않은 상태)")
    @Test
    public void Follow_Fail_When_Unauthenticated() throws Exception {

        // given
        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/follow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 성공")
    @Test
    public void Follow_Success() throws Exception {

        // given
        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/follow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Long savedFollowId = objectMapper.readValue(json, Long.class);

                    assertThat(savedFollowId).isNotNull();

                    boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
                    assertThat(isFollowing).isTrue();
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 실패(url의 targetId가 잘못된 경우")
    @Test
    public void UnFollow_Fail_When_WrongTargetId() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        Long wrongId = 999999L;
        String url = "/api/v1/accounts/" + wrongId + "/unfollow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("언 팔로우 - 실패(로그인하지 않은 상태)")
    @Test
    public void UnFollow_Fail_When_Unauthenticated() throws Exception {

        // given
        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/unfollow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 성공")
    @Test
    public void UnFollow_Success() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/unfollow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Long removedFollowId = objectMapper.readValue(json, Long.class);

                    assertThat(removedFollowId).isNotNull();
                });

        isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isFalse();
    }
}