package com.brandpark.sharemusic.api.album.query;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AlbumQueryRepositoryTest {

    @Autowired AlbumQueryRepository queryRepository;
    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumRepository albumRepository;
    @Autowired AccountRepository accountRepository;
    Account user;
    Album savedAlbum;

    @BeforeEach
    public void setUp() {
        user = accountFactory.createAccount("닉네임");
        accountRepository.save(user);
        
        savedAlbum = albumFactory.createAlbumWithTracks("저장되어있는 앨범", 5, user.getId());
        albumRepository.save(savedAlbum);

        for (int i = 0; i < 30; i++) {
            albumRepository.save(albumFactory.createAlbumWithTracks("또다른 앨범" + i, 5, user.getId()));
        }

    }

    @DisplayName("한 페이지 앨범의 간략한 정보 조회")
    @Test
    public void RetrieveAlbumShortDto() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<AlbumShortDto> result = queryRepository.findAllAlbumShortDto(pageRequest);

        List<AlbumShortDto> pageContent = result.getContent();
        AlbumShortDto resultOne = pageContent.get(0);

        // then
        assertThat(result.getTotalElements()).isEqualTo(31);
        assertThat(pageContent.size()).isEqualTo(10);
        assertThat(resultOne.getTitle()).isEqualTo(savedAlbum.getTitle());
        assertThat(resultOne.getAlbumImage()).isEqualTo(savedAlbum.getAlbumImage());
        assertThat(resultOne.getDescription()).isEqualTo(savedAlbum.getDescription());
        assertThat(resultOne.getTrackCount()).isEqualTo(savedAlbum.getTrackCount());
        assertThat(resultOne.getCreator()).isEqualTo(user.getNickname());
        assertThat(resultOne.getCreatorProfileImage()).isEqualTo(user.getProfileImage());
    }
}