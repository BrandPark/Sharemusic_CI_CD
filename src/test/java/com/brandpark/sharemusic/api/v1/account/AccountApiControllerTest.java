package com.brandpark.sharemusic.api.v1.account;

import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.follow.domain.Follow;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.fasterxml.jackson.core.type.TypeReference;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class AccountApiControllerTest {

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

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 실패(이미 팔로우 중인 경우)")
    @Test
    public void Follow_Fail_When_AlreadyFollowing() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        assertThat(followRepository.isFollowing(myAccount.getId(), otherAccount.getId()));

        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/follow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
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

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 실패(이미 언팔로우 상태)")
    @Test
    public void UnFollow_Fail_When_AlreadyUnfollow() throws Exception {

        // given
        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isFalse();

        // when, then
        String url = "/api/v1/accounts/" + otherAccount.getId() + "/unfollow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
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
                    assertThat(followRepository.findById(removedFollowId)).isEmpty();
                });

        isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isFalse();
    }

    @DisplayName("팔로워 리스트 페이징 조회 - 실패(잘못된 targetId)")
    @Test
    public void SearchFollowersByPaging_Fail_When_WrongTargetId() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(otherAccount)
                .target(myAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(otherAccount.getId(), myAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        Long wrongTargetId = 9999999999L;
        String url = "/api/v1/accounts/" + wrongTargetId + "/followers";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로워 리스트 페이징 조회 - 성공")
    @Test
    public void SearchFollowersByPaging_Success() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(otherAccount)
                .target(myAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(otherAccount.getId(), myAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        String url = "/api/v1/accounts/" + myAccount.getId() + "/followers";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PagingDto<FollowerInfoDto> resultPage = objectMapper.readValue(json, new TypeReference<PagingDto<FollowerInfoDto>>() {});

                    assertThat(resultPage.getPageSize()).isEqualTo(6);
                    assertThat(resultPage.getNumberOfElements()).isEqualTo(1);

                    List<FollowerInfoDto> followers = resultPage.getContents();
                    assertThat(followers.size()).isEqualTo(1);
                    assertThat(followers.get(0).getNickname()).isEqualTo(otherAccount.getNickname());
                });
    }

    @DisplayName("팔로잉 리스트 페이징 조회 - 실패(잘못된 targetId)")
    @Test
    public void SearchFollowingsByPaging_Fail_When_WrongTargetId() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        Long wrongTargetId = 9999999999L;
        String url = "/api/v1/accounts/" + wrongTargetId + "/followings";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    ExceptionResult exceptionResult = objectMapper.readValue(json, ExceptionResult.class);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로잉 리스트 페이징 조회 - 성공")
    @Test
    public void SearchFollowingsByPaging_Success() throws Exception {

        // given
        Follow follow = followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), otherAccount.getId());
        assertThat(isFollowing).isTrue();

        // when, then
        String url = "/api/v1/accounts/" + myAccount.getId() + "/followings";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PagingDto<FollowingInfoDto> resultPage = objectMapper.readValue(json, new TypeReference<PagingDto<FollowingInfoDto>>() {});

                    assertThat(resultPage.getPageSize()).isEqualTo(6);
                    assertThat(resultPage.getNumberOfElements()).isEqualTo(1);

                    List<FollowingInfoDto> followings = resultPage.getContents();
                    assertThat(followings.size()).isEqualTo(1);
                    assertThat(followings.get(0).getNickname()).isEqualTo(otherAccount.getNickname());
                });
    }
}