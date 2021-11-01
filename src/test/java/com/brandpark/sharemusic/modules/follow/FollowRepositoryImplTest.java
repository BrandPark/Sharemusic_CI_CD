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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FollowRepositoryImplTest {

    @Autowired FollowRepository followRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    Account myAccount;
    Account unfollowingOtherAccount;
    List<Account> followers = new ArrayList<>();
    List<Account> followings = new ArrayList<>();
    List<Follow> followListToFollowers = new ArrayList<>();
    List<Follow> followListToFollowings = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("내 계정");
        unfollowingOtherAccount = accountFactory.createAccount("팔로우 하지 않은 계정");
        accountRepository.saveAll(List.of(myAccount, unfollowingOtherAccount));

        for (int i = 0; i < 30; i++) {
            followers.add(accountFactory.createAccount("나를 팔로우한 계정" + i));
        }

        for (int i = 0; i < 30; i++) {
            followings.add(accountFactory.createAccount("내가 팔로우한 계정" + i));
        }
        accountRepository.saveAll(Stream.concat(followers.stream(), followings.stream()).collect(Collectors.toList()));

        for (Account follower : followers) {
            followListToFollowers.add(Follow.builder()
                    .follower(follower)
                    .target(myAccount)
                    .build());
        }

        for (Account following : followings) {
            followListToFollowings.add(Follow.builder()
                    .follower(myAccount)
                    .target(following)
                    .build());
        }

        followRepository.saveAll(Stream.concat(followListToFollowers.stream(), followListToFollowings.stream()).collect(Collectors.toList()));
    }

    @DisplayName("팔로잉 중인지 확인")
    @Test
    public void CheckQuery_IsFollowing() throws Exception {

        // given
        // when
        boolean following = followRepository.isFollowing(myAccount.getId(), followListToFollowings.get(0).getId());
        boolean notFollowing = followRepository.isFollowing(myAccount.getId(), unfollowingOtherAccount.getId());

        // then
        assertThat(following).isTrue();
        assertThat(notFollowing).isFalse();
    }
}