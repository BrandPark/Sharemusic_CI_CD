package com.brandpark.sharemusic.modules.account.domain;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AccountRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumRepository albumRepository;
    @Autowired AlbumFactory albumFactory;
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

}