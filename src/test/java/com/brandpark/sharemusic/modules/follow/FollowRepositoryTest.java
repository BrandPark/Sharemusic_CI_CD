package com.brandpark.sharemusic.modules.follow;

import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.follow.domain.Follow;
import com.brandpark.sharemusic.modules.account.follow.domain.FollowRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.FollowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class FollowRepositoryTest {

    @Autowired FollowRepository followRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired FollowFactory followFactory;
    Account myAccount;
    Account unfollowingOtherAccount;
    List<Account> followers;
    List<Account> followings;
    List<Follow> followListAboutFollowers;
    List<Follow> followListAboutFollowings;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("내 계정");
        unfollowingOtherAccount = accountFactory.persistAccount("팔로우 하지 않은 계정");

        followers = accountFactory.persistAccountList("나를 팔로우한 계정", 30);
        followings = accountFactory.persistAccountList("내가 팔로우한 계정", 30);

        followListAboutFollowers = followFactory.persistFollowers(myAccount, followers);
        followListAboutFollowings = followFactory.persistFollowings(myAccount, followings);
    }

    @DisplayName("팔로잉 중인지 확인")
    @Test
    public void CheckQuery_IsFollowing() throws Exception {

        // given
        // when
        boolean following = followRepository.isFollowing(myAccount.getId(), followings.get(0).getId());
        boolean notFollowing = followRepository.isFollowing(myAccount.getId(), unfollowingOtherAccount.getId());

        // then
        assertThat(following).isTrue();
        assertThat(notFollowing).isFalse();
    }

    @DisplayName("followerId와 targetId로 팔로워 조회")
    @Test
    public void findByFollowerIdAndTargetId() throws Exception {

        // given
        Long followerId = myAccount.getId();
        Long targetId = followings.get(0).getId();

        // when
        Optional<Follow> follow = followRepository.findByFollowerIdAndTargetId(followerId, targetId);
        Optional<Follow> notFollow = followRepository.findByFollowerIdAndTargetId(followerId, unfollowingOtherAccount.getId());

        // then
        assertThat(follow).isNotEmpty();
        assertThat(follow.get().getFollower().getId()).isEqualTo(followerId);
        assertThat(follow.get().getTarget().getId()).isEqualTo(targetId);

        assertThat(notFollow).isEmpty();
    }

    @DisplayName("내가 팔로우한 사람 수 조회")
    @Test
    public void countAllByFollowerId() throws Exception {

        // given

        // when
        int followerCount = followRepository.countAllByFollowerId(myAccount.getId());

        // then
        assertThat(followerCount).isEqualTo(followings.size());
    }
}