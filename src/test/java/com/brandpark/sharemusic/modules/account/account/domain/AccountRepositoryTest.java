package com.brandpark.sharemusic.modules.account.account.domain;

import com.brandpark.sharemusic.modules.account.account.form.FriendshipDataForm;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.account.follow.domain.Follow;
import com.brandpark.sharemusic.modules.account.follow.domain.FollowRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.FollowFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AccountRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired AlbumRepository albumRepository;
    @Autowired FollowRepository followRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumFactory albumFactory;
    @Autowired FollowFactory followFactory;
    @Autowired EntityManager entityManager;
    Account myAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("testAccount");
        otherAccount = accountFactory.persistAccount("otherAccount");
    }

    @Test
    void findByEmailOrNickname() {

        Account byNickname = accountRepository.findByEmailOrNickname(myAccount.getNickname()).get();
        Account byEmail = accountRepository.findByEmailOrNickname(myAccount.getEmail()).get();
        assertThat(byNickname).isNotNull();
        assertThat(byNickname == byEmail).isTrue();
    }

    @Test
    public void findByAlbumId() throws Exception {

        // given
        Account account = accountFactory.createAccount("새 계정");
        accountRepository.save(account);

        Album album = albumFactory.createAlbumWithTracks("새 앨범", 5, account.getId());
        albumRepository.save(album);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Account> result = accountRepository.findByAlbumId(album.getId());

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(account.getId());
        assertThat(result.get().getNickname()).isEqualTo(account.getNickname());
    }

    @Test
    public void findAllByFollowingTargetId() throws Exception {

        // given
        Account myAccount = accountFactory.createAccount("내 계정");
        List<Account> followers = accountFactory.createAccountList("팔로워 들", 5);

        accountRepository.saveAll(
                Stream.concat(followers.stream(), List.of(myAccount).stream())
                        .collect(Collectors.toList())
        );

        followFactory.persistFollowers(myAccount, followers);

        // when
        List<Account> result = accountRepository.findAllFollowersByFollowingTargetId(myAccount.getId());

        // then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getNickname()).contains("팔로워 들");
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
        FriendshipDataForm friendshipData = accountRepository.findFriendshipData(myAccount.getId());

        // then
        assertThat(friendshipData.getAlbumCount()).isEqualTo(albums.size());
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
        FriendshipDataForm result = accountRepository.findFriendshipData(myAccount.getId());

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
        FriendshipDataForm result = accountRepository.findFriendshipData(myAccount.getId());

        // then
        assertThat(result.getFollowingCount()).isEqualTo(1);
    }
}