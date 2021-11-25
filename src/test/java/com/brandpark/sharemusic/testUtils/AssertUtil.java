package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackInfoResponse;
import com.brandpark.sharemusic.modules.album.domain.Album;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AssertUtil {

    public static <T> void assertPage(int pageNum, int pageSize, int totalElementCount, PageResult<T> page) {
        assertThat(page.getPageNumber()).isEqualTo(pageNum);
        assertThat(page.getPageSize()).isEqualTo(pageSize);
        assertThat(page.getOffset()).isEqualTo(pageSize * pageNum);
        assertThat(page.getTotalPages()).isGreaterThan(0);
        assertThat(page.getNumberOfElements()).isGreaterThan(0);
        assertThat(page.getTotalElements()).isEqualTo(totalElementCount);
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

    public static void assertEntityIsNotEmpty(Album album, CreateAlbumRequest reqDto) {
        assertThat(album.getId()).isNotNull();
        assertThat(album.getTitle()).isEqualTo(reqDto.getTitle());
        assertThat(album.getDescription()).isEqualTo(reqDto.getDescription());
        assertThat(album.getAlbumImage()).isEqualTo(reqDto.getAlbumImage());
        assertThat(album.getTrackCount()).isGreaterThan(0);
        assertThat(album.getCreatedDate()).isNotNull();
        assertThat(album.getModifiedDate()).isNotNull();
        assertThat(album.getAccountId()).isNotNull();
    }
}
