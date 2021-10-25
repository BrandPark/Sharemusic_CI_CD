package com.brandpark.sharemusic.modules.follow;

import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FollowRepositoryImplTest {

    @Autowired FollowRepository followRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    Account myAccount;
    Account followingOtherAccount;
    Account unfollowingOtherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("내 계정");
        followingOtherAccount = accountFactory.createAccount("팔로우한 다른 사람 계정");
        unfollowingOtherAccount = accountFactory.createAccount("팔로우하지 않은 다른 사람 계정");
        accountRepository.saveAll(List.of(myAccount, followingOtherAccount, unfollowingOtherAccount));

        followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(followingOtherAccount)
                .build());
    }

    @DisplayName("팔로우 상태 확인 쿼리")
    @Test
    public void CheckQuery_IsFollowing() throws Exception {

        // given
        // when
        boolean isFollowing = followRepository.isFollowing(myAccount.getId(), followingOtherAccount.getId());
        boolean isFollowing2 = followRepository.isFollowing(myAccount.getId(), unfollowingOtherAccount.getId());

        // then
        assertThat(isFollowing).isTrue();
        assertThat(isFollowing2).isFalse();
    }
}