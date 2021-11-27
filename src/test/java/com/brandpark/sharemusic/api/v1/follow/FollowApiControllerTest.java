package com.brandpark.sharemusic.api.v1.follow;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowerInfoResponse;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowingInfoResponse;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.FollowRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.FollowFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class FollowApiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired FollowRepository followRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired FollowFactory followFactory;
    @Autowired ObjectMapper objectMapper;
    Account otherAccount;
    Account myAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("내 계정");
        otherAccount = accountFactory.persistAccount("다른 사람 계정");
    }

    @DisplayName("팔로워 리스트 페이징 조회 - 실패(조회 대상이 없는 계정인 경우)")
    @Test
    public void FindFollowersByPaging_Fail_When_NotFoundTargetAccount() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        Long wrongTargetId = 9999999999L;
        assertThat(accountRepository.existsById(wrongTargetId)).isFalse();

        // when, then
        String url = "/api/v1/accounts/" + wrongTargetId + "/followers";
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);
                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로워 리스트 페이징 조회 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void FindFollowersByPaging_Success_When_FullFirstPage() throws Exception {

        // given
        List<Account> followers = accountFactory.persistAccountList("팔로워", 12);
        followFactory.persistFollowers(myAccount, followers);

        int totalFollowerCounts = followers.size();
        int pageSize = 10;
        int fullPageNum = 0;

        int expectedFindResultCount = 10;

        String url = "/api/v1/accounts/" + myAccount.getId() + "/followers";

        // when, then
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(fullPageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<FollowerInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(fullPageNum, pageSize, totalFollowerCounts, resultPage);

                    List<FollowerInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    FollowerInfoResponse resultOne = result.get(0);
                    assertFollowerInfo(resultOne);
                });
    }

    @DisplayName("팔로워 리스트 페이징 조회 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void FindFollowersByPaging_Success_When_LessLastPage() throws Exception {

        // given
        List<Account> followers = accountFactory.persistAccountList("팔로워", 12);
        followFactory.persistFollowers(myAccount, followers);

        int totalFollowerCounts = followers.size();
        int pageSize = 10;
        int lessLastPageNum = 1;

        int expectedFindResultCount = 2;

        String url = "/api/v1/accounts/" + myAccount.getId() + "/followers";

        // when, then
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(lessLastPageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<FollowerInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(lessLastPageNum, pageSize, totalFollowerCounts, resultPage);

                    List<FollowerInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    FollowerInfoResponse resultOne = result.get(0);
                    assertFollowerInfo(resultOne);
                });
    }

    @DisplayName("팔로잉 리스트 페이징 조회 - 실패(조회 대상이 없는 계정인 경우)")
    @Test
    public void FindFollowingsByPaging_Fail_When_NotFoundTargetAccount() throws Exception {

        // given
        int pageNum = 0;
        int pageSize = 10;

        Long wrongTargetId = 9999999999L;
        assertThat(accountRepository.existsById(wrongTargetId)).isFalse();

        String url = "/api/v1/accounts/" + wrongTargetId + "/followings";

        // when, then
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로잉 리스트 페이징 조회 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void FindFollowingsByPaging_Success_When_FullFirstPage() throws Exception {

        // given
        List<Account> followings = accountFactory.persistAccountList("팔로잉", 12);
        followFactory.persistFollowings(myAccount, followings);

        int totalFollowingCount = followings.size();
        int pageNum = 0;
        int pageSize = 10;

        int expectedFindResultCount = 10;

        String url = "/api/v1/accounts/" + myAccount.getId() + "/followings";

        // when, then
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<FollowingInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {});

                    AssertUtil.assertPageResult(pageNum, pageSize, totalFollowingCount, resultPage);

                    List<FollowingInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    FollowingInfoResponse resultOne = result.get(0);
                    assertFollowingInfo(resultOne);
                });
    }

    @DisplayName("팔로잉 리스트 페이징 조회 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void FindFollowingsByPaging_Success_When_LessLastPage() throws Exception {

        // given
        List<Account> followings = accountFactory.persistAccountList("팔로잉", 12);
        followFactory.persistFollowings(myAccount, followings);

        int totalFollowingCount = followings.size();
        int pageSize = 10;
        int pageNum = 1;

        int expectedFindResultCount = 2;

        String url = "/api/v1/accounts/" + myAccount.getId() + "/followings";

        // when, then
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {

                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<FollowingInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {});

                    AssertUtil.assertPageResult(pageNum, pageSize, totalFollowingCount, resultPage);

                    List<FollowingInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedFindResultCount);

                    FollowingInfoResponse resultOne = result.get(0);
                    assertFollowingInfo(resultOne);
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 실패(팔로우 대상이 없는 계정인 경우)")
    @Test
    public void Follow_Fail_When_NotFoundTargetAccount() throws Exception {

        // given
        Long wrongId = 999999L;
        assertThat(accountRepository.existsById(wrongId)).isFalse();

        // when, then
        String url = "/api/v1/accounts/" + wrongId + "/follow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 실패(이미 팔로우 중인 경우)")
    @Test
    public void Follow_Fail_When_AlreadyFollowing() throws Exception {

        // given
        Account followingAccount = accountFactory.persistAccount("팔로잉");
        followFactory.persistFollowing(myAccount, followingAccount);

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), followingAccount.getId());
        assertThat(isFollowing).isTrue();

        String url = "/api/v1/accounts/" + followingAccount.getId() + "/follow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 실패(자기 자신을 팔로우 시도)")
    @Test
    public void Follow_Fail_When_FollowMyself() throws Exception {

        // given
        String url = "/api/v1/accounts/" + myAccount.getId() + "/follow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @DisplayName("팔로우 - 실패(로그인하지 않은 상태)")
    @Test
    public void Follow_Fail_When_Unauthenticated() throws Exception {

        // given
        Account followingAccount = accountFactory.persistAccount("팔로잉");
        followFactory.persistFollowing(myAccount, followingAccount);

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), followingAccount.getId());
        assertThat(isFollowing).isTrue();

        String url = "/api/v1/accounts/" + followingAccount.getId() + "/follow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("팔로우 - 성공")
    @Test
    public void Follow_Success() throws Exception {

        // given
        Account notFollowingAccount = otherAccount;

        boolean isNotFollowing = !followRepository.isFollowing(myAccount.getId(), notFollowingAccount.getId());
        assertThat(isNotFollowing).isTrue();

        String url = "/api/v1/accounts/" + notFollowingAccount.getId() + "/follow";

        // when
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Long savedFollowId = objectMapper.readValue(json, Long.class);

                    assertThat(savedFollowId).isNotNull();
                });

        // then
        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), notFollowingAccount.getId());
        assertThat(isFollowing).isTrue();
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 실패(언 팔로우 대상이 없는 계정인 경우)")
    @Test
    public void UnFollow_Fail_When_NotFoundTargetAccount() throws Exception {

        // given
        Long wrongTargetId = 999999L;
        assertThat(accountRepository.existsById(wrongTargetId)).isFalse();

        String url = "/api/v1/accounts/" + wrongTargetId + "/unfollow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.NOT_FOUND_ACCOUNT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 실패(팔로잉 중인 대상이 아닐 경우)")
    @Test
    public void UnFollow_Fail_When_NotFollowingState() throws Exception {

        // given
        Account notFollowingAccount = otherAccount;

        boolean isNotFollowing = !followRepository.isFollowing(myAccount.getId(), notFollowingAccount.getId());
        assertThat(isNotFollowing).isTrue();

        // when, then
        String url = "/api/v1/accounts/" + notFollowingAccount.getId() + "/unfollow";
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {

                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ACCESS_EXCEPTION.getCode());
                });
    }

    @DisplayName("언 팔로우 - 실패(로그인하지 않은 상태)")
    @Test
    public void UnFollow_Fail_When_Unauthenticated() throws Exception {

        // given
        Account followingAccount = accountFactory.persistAccount("팔로잉");
        followFactory.persistFollowing(myAccount, followingAccount);

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), followingAccount.getId());
        assertThat(isFollowing).isTrue();

        String url = "/api/v1/accounts/" + followingAccount.getId() + "/unfollow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "내 계정", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("언 팔로우 - 성공")
    @Test
    public void UnFollow_Success() throws Exception {

        // given
        Account followingAccount = accountFactory.persistAccount("팔로잉");
        followFactory.persistFollowing(myAccount, followingAccount);

        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), followingAccount.getId());
        assertThat(isFollowing).isTrue();

        String url = "/api/v1/accounts/" + followingAccount.getId() + "/unfollow";

        // when, then
        mockMvc.perform(post(url)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Long removedFollowId = objectMapper.readValue(json, Long.class);

                    assertThat(removedFollowId).isNotNull();
                    assertThat(followRepository.findById(removedFollowId)).isEmpty();
                });

        boolean isNotFollowing = !followRepository.isFollowing(myAccount.getId(), followingAccount.getId());
        assertThat(isNotFollowing).isTrue();
    }

    private ExceptionResult getExceptionResult(MvcResult result) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        return objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ExceptionResult.class);
    }

    private void assertFollowerInfo(FollowerInfoResponse resultOne) {
        assertThat(resultOne.getFollowerId()).isNotNull();
        assertThat(resultOne.getNickname()).containsIgnoringCase("팔로워");
        assertThat(resultOne.getName()).containsIgnoringCase("팔로워");
        assertThat(resultOne.getProfileImage()).isNotNull();
        assertThat(resultOne.getFollowDate()).isNotNull();
    }

    private void assertFollowingInfo(FollowingInfoResponse resultOne) {
        assertThat(resultOne.getFollowingId()).isNotNull();
        assertThat(resultOne.getName()).containsIgnoringCase("팔로잉");
        assertThat(resultOne.getNickname()).containsIgnoringCase("팔로잉");
        assertThat(resultOne.getProfileImage()).isNotNull();
        assertThat(resultOne.getFollowingDate()).isNotNull();
    }
}