package com.brandpark.sharemusic.api.v1.account.query;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.dto.FriendshipDataResponse;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.follow.domain.Follow;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AccountQueryRepositoryTest {

    @Autowired AlbumRepository albumRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired FollowRepository followRepository;
    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountQueryRepository accountQueryRepository;

    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.createAccount("myAccount");
        otherAccount = accountFactory.createAccount("otherAccount");

        accountRepository.saveAll(List.of(myAccount, otherAccount));
    }

    @DisplayName("앨범 수 가져오기")
    @Test
    public void AlbumCount() throws Exception {

        // given
        Album album1 = albumFactory.createAlbumWithTracks("앨범1", 5, myAccount.getId());
        Album album2 = albumFactory.createAlbumWithTracks("앨범2", 5, myAccount.getId());

        List<Album> albums = new ArrayList<>(List.of(album1, album2));
        albumRepository.saveAll(albums);

        // when
        FriendshipDataResponse activityData = accountQueryRepository.findFriendshipData(myAccount.getId());

        // then
        assertThat(activityData.getAlbumCount()).isEqualTo(albums.size());
    }

    @DisplayName("팔로워 수 가져오기")
    @Test
    public void FollowerCount() throws Exception {

        // given
        followRepository.save(Follow.builder()
                .follower(otherAccount)
                .target(myAccount)
                .build());

        // when
        FriendshipDataResponse result = accountQueryRepository.findFriendshipData(myAccount.getId());

        // then
        assertThat(result.getFollowerCount()).isEqualTo(1);
    }

    @DisplayName("팔로잉 수 가져오기")
    @Test
    public void FollowingCount() throws Exception {

        // given
        followRepository.save(Follow.builder()
                .follower(myAccount)
                .target(otherAccount)
                .build());

        // when
        FriendshipDataResponse result = accountQueryRepository.findFriendshipData(myAccount.getId());

        // then
        assertThat(result.getFollowingCount()).isEqualTo(1);
    }

    @DisplayName("팔로워 리스트 가져오기")
    @Test
    public void FollowerList() throws Exception {

        // given
        int followerCount = 30;

        Pageable pageable = PageRequest.of(0, 6);

        List<Follow> followEntityList = new ArrayList<>();
        List<Account> followerList = new ArrayList<>();
        for (int i = 0; i < followerCount; i++) {
            Account follower = accountFactory.createAccount("follower" + i);
            followerList.add(follower);

            followEntityList.add(Follow.builder()
                    .follower(follower)
                    .target(myAccount)
                    .build());
        }
        accountRepository.saveAll(followerList);
        followRepository.saveAll(followEntityList);

        // when
        PagingDto<FollowerInfoDto> followers = accountQueryRepository.findAllFollowersByPaging(myAccount.getId(), pageable);

        // then
        List<FollowerInfoDto> contents = followers.getContents();
        assertThat(contents.size()).isEqualTo(followers.getNumberOfElements());
        assertThat(contents.get(0).getFollowDate()).isBeforeOrEqualTo(contents.get(1).getFollowDate());
        assertThat(contents.get(0).getName()).contains("follower");
        assertThat(contents.get(0).getNickname()).contains("follower");

        assertThat(followers.getTotalElements()).isEqualTo(followerCount);
        assertThat(followers.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(followers.getTotalPages()).isEqualTo(followerCount / pageable.getPageSize());
        assertThat(followers.getNumberOfElements()).isEqualTo(pageable.getPageSize());

    }

    @DisplayName("팔로잉 리스트 가져오기")
    @Test
    public void FollowingList() throws Exception {

        // given
        int followingCount = 30;

        Pageable pageable = PageRequest.of(0, 6);

        List<Follow> followEntityList = new ArrayList<>();
        List<Account> followingList = new ArrayList<>();
        for (int i = 0; i < followingCount; i++) {
            Account followingTarget = accountFactory.createAccount("followingTarget" + i);
            followingList.add(followingTarget);

            followEntityList.add(Follow.builder()
                    .follower(myAccount)
                    .target(followingTarget)
                    .build());
        }
        accountRepository.saveAll(followingList);
        followRepository.saveAll(followEntityList);

        // when
        PagingDto<FollowingInfoDto> followings = accountQueryRepository.findAllFollowingsByPaging(myAccount.getId(), pageable);

        // then
        List<FollowingInfoDto> contents = followings.getContents();
        assertThat(contents.size()).isEqualTo(followings.getNumberOfElements());
        assertThat(contents.get(0).getFollowingDate()).isBeforeOrEqualTo(contents.get(1).getFollowingDate());
        assertThat(contents.get(0).getName()).contains("following");
        assertThat(contents.get(0).getNickname()).contains("following");

        assertThat(followings.getTotalElements()).isEqualTo(followingCount);
        assertThat(followings.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(followings.getTotalPages()).isEqualTo(followingCount / pageable.getPageSize());
        assertThat(followings.getNumberOfElements()).isEqualTo(pageable.getPageSize());
    }
}