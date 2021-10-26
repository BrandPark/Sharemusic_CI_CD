package com.brandpark.sharemusic.api.v1.account.query;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.api.v1.account.query.dto.ActivityDataResponse;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.follow.Follow;
import com.brandpark.sharemusic.modules.follow.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        ActivityDataResponse activityData = accountQueryRepository.findActivityData(myAccount.getId());

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
        ActivityDataResponse result = accountQueryRepository.findActivityData(myAccount.getId());

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
        ActivityDataResponse result = accountQueryRepository.findActivityData(myAccount.getId());

        // then
        assertThat(result.getFollowingCount()).isEqualTo(1);
    }
}