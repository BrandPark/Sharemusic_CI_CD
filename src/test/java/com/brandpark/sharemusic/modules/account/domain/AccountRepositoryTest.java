package com.brandpark.sharemusic.modules.account.domain;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.FollowFactory;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    Account account;

    @BeforeEach
    public void setUp() {
        account = accountFactory.createAccount("testAccount");
        accountRepository.save(account);
    }

    @Test
    void findByEmailOrNickname() {

        Account byNickname = accountRepository.findByEmailOrNickname(account.getNickname()).get();
        Account byEmail = accountRepository.findByEmailOrNickname(account.getEmail()).get();
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

        followRepository.saveAll(followFactory.createFollowRelationship(myAccount, followers));

        // when
        List<Account> result = accountRepository.findAllFollowersByFollowingTargetId(myAccount.getId());

        // then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getNickname()).contains("팔로워 들");
    }
}