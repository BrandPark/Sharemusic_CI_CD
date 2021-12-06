package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse.TrackInfoResponse;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.brandpark.sharemusic.testUtils.AssertUtil.assertDtoIsNotEmpty;
import static com.brandpark.sharemusic.testUtils.AssertUtil.assertPageResult;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AlbumQueryRepositoryTest {

    @Autowired AlbumQueryRepository albumQueryRepository;
    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumRepository albumRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired CommentRepository commentRepository;

    @DisplayName("앨범정보 모두 조회")
    @Test
    public void FindAllAlbumsInfoResponse() throws Exception {

        // given
        Account accountHasAlbum = accountFactory.persistAccount("accountHasAlbum");
        int albumCount = 12;
        int trackCount = 10;
        albumFactory.persistAlbumsWithTracks("앨범", albumCount, trackCount, accountHasAlbum.getId());

        assertThat(albumRepository.count()).isEqualTo(albumCount);

        int pageSize = 5;
        PageRequest request = PageRequest.of(0, pageSize);

        // when
        PageResult<AlbumInfoResponse> resultPage = albumQueryRepository.findAllAlbumsInfo(request);

        // then
        assertPageResult(0, pageSize, albumCount, resultPage);

        List<AlbumInfoResponse> resultAlbums = resultPage.getContent();
        AlbumInfoResponse albumOne = resultAlbums.get(0);
        List<TrackInfoResponse> resultTracks = albumOne.getTracks();
        TrackInfoResponse trackOne = resultTracks.get(0);

        assertThat(resultAlbums.size()).isEqualTo(pageSize);
        assertDtoIsNotEmpty(albumOne);

        assertThat(resultTracks.size()).isEqualTo(trackCount);
        assertDtoIsNotEmpty(trackOne);
    }
}