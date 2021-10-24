package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumListPagingDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.TrackDetailDto;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired CommentRepository commentRepository;
    Account user;
    Album firstAlbum;
    List<Album> savedAlbums = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = accountFactory.createAccount("닉네임");
        accountRepository.save(user);

        firstAlbum = albumFactory.createAlbumWithTracks("처음 생성된 앨범", 5, user.getId());
        albumRepository.save(firstAlbum);
        savedAlbums.add(firstAlbum);

        createComments();
        createAndAddAlbums(savedAlbums);
    }

    @DisplayName("한 페이지 앨범의 간략한 정보 DB 생성날짜 내림차순 조회 - 성공")
    @Test
    public void RetrieveAlbumShortDto() throws Exception {

        // given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        AlbumListPagingDto responseDto = queryRepository.findAllAlbumShortDto(pageRequest);

        List<AlbumShortDto> pageContent = responseDto.getAlbums();
        AlbumShortDto resultFirst = pageContent.get(0);

        // then
        Album lastCreateAlbum = savedAlbums.get(savedAlbums.size() - 1);

        assertThat(responseDto.getTotalElements()).isEqualTo(savedAlbums.size());
        assertThat(pageContent.size()).isEqualTo(10);
        assertThat(resultFirst.getId()).isEqualTo(lastCreateAlbum.getId());
        assertThat(resultFirst.getTitle()).isEqualTo(lastCreateAlbum.getTitle());
        assertThat(resultFirst.getAlbumImage()).isEqualTo(lastCreateAlbum.getAlbumImage());
        assertThat(resultFirst.getDescription()).isEqualTo(lastCreateAlbum.getDescription());
        assertThat(resultFirst.getTrackCount()).isEqualTo(lastCreateAlbum.getTrackCount());
        assertThat(resultFirst.getCreator()).isEqualTo(user.getNickname());
        assertThat(resultFirst.getCreatorProfileImage()).isEqualTo(user.getProfileImage());
    }

    @DisplayName("앨범의 디테일 정보 DB 조회")
    @Test
    public void RetrieveAlbumDetailDto() throws Exception {

        // given
        Long retrieveAlbumId = firstAlbum.getId();

        // when
        AlbumDetailDto result = queryRepository.findAlbumDetailDtoById(retrieveAlbumId);

        // then
        assertThat(result.getId()).isEqualTo(firstAlbum.getId());
        assertThat(result.getTitle()).isEqualTo(firstAlbum.getTitle());
        assertThat(result.getAlbumImage()).isEqualTo(firstAlbum.getAlbumImage());
        assertThat(result.getDescription()).isEqualTo(firstAlbum.getDescription());
        assertThat(result.getCreator()).isEqualTo(user.getNickname());
        assertThat(result.getCreatorProfileImage()).isEqualTo(user.getProfileImage());

        List<TrackDetailDto> tracks = result.getTracks();
        assertThat(tracks.size()).isEqualTo(firstAlbum.getTracks().size());
        assertThat(tracks.get(0).getId()).isEqualTo(firstAlbum.getTracks().get(0).getId());
        assertThat(tracks.get(0).getName()).isEqualTo(firstAlbum.getTracks().get(0).getName());
        assertThat(tracks.get(0).getArtist()).isEqualTo(firstAlbum.getTracks().get(0).getArtist());
    }

    private void createAndAddAlbums(List<Album> albums) {
        for (int i = 0; i < 30; i++) {
            Album album = albumFactory.createAlbumWithTracks("또다른 앨범" + i, 5, user.getId());
            albumRepository.save(album);

            albums.add(album);
        }
    }

    private void createComments() {
        for (int i = 0; i < 9; i++) {
            Comment comment = albumFactory.createComment(firstAlbum.getId(), user.getId(), firstAlbum.getTitle() + ".댓글_" + i);
            commentRepository.save(comment);
        }
    }
}