package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.TrackInfoResponse;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AssertUtil {

    public static <T> void assertPageResult(int pageNum, int pageSize, int totalElementCount, PageResult<T> page) {
        assertThat(page.getPageNumber()).isEqualTo(pageNum);
        assertThat(page.getPageSize()).isEqualTo(pageSize);
        assertThat(page.getOffset()).isEqualTo(pageSize * pageNum);
        assertThat(page.getTotalPages()).isGreaterThan(0);
        assertThat(page.getNumberOfElements()).isGreaterThan(0);
        assertThat(page.getTotalElements()).isEqualTo(totalElementCount);
    }

    public static <T> void assertPage(int pageNum, int pageSize, int totalElementCount, Page page) {
        assertThat(page.getTotalElements()).isEqualTo(totalElementCount);
        assertThat(page.getNumber()).isEqualTo(pageNum);
        assertThat(page.getPageable().getPageSize()).isEqualTo(pageSize);
        assertThat(page.getPageable().getPageNumber()).isEqualTo(pageNum);
        assertThat(page.getPageable().getOffset()).isEqualTo(pageNum * pageSize);
    }

    public static void assertDtoIsNotEmpty(AlbumInfoResponse dto) {
        assertThat(dto.getAlbumId()).isNotNull();
        assertThat(dto.getTitle()).isNotNull();
        assertThat(dto.getDescription()).isNotNull();
        assertThat(dto.getAlbumImage()).isNotNull();
        assertThat(dto.getTrackCount()).isNotNull();
        assertThat(dto.getAccountId()).isNotNull();
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getModifiedDate()).isNotNull();
    }

    public static void assertDtoIsNotEmpty(TrackInfoResponse dto) {
        assertThat(dto.getTrackId()).isNotNull();
        assertThat(dto.getName()).isNotNull();
        assertThat(dto.getArtist()).isNotNull();
    }

    public static void assertEntityIsNotEmpty(Album album) {
        assertThat(album.getId()).isNotNull();
        assertThat(album.getTitle()).isNotNull();
        assertThat(album.getDescription()).isNotNull();
        assertThat(album.getAlbumImage()).isNotNull();
        assertThat(album.getTrackCount()).isGreaterThan(0);
        assertThat(album.getCreatedDate()).isNotNull();
        assertThat(album.getModifiedDate()).isNotNull();
        assertThat(album.getAccountId()).isNotNull();
    }

    public static void assertEntityIsNotEmpty(Comment comment) {
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getAlbumId()).isNotNull();
        assertThat(comment.getAccountId()).isNotNull();
        assertThat(comment.getContent()).isNotNull();
        assertThat(comment.getCreatedDate()).isNotNull();
        assertThat(comment.getModifiedDate()).isNotNull();
    }
}
