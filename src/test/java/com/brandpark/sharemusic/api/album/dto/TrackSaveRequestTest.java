package com.brandpark.sharemusic.api.album.dto;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrackSaveRequestTest {

    AlbumFactory albumFactory = new AlbumFactory();

    @DisplayName("TrackSaveRequest -> Track 도메인 생성")
    @Test
    void toEntity() {

        // given
        Album album = albumFactory.createAlbum("제목");
        TrackSaveRequest dto = albumFactory.createTrackSaveDto("음원명", "아티스트");

        // when
        Track track = dto.toEntity(album);

        // then
        assertThat(track.getName()).isEqualTo(dto.getName());
        assertThat(track.getArtist()).isEqualTo(dto.getArtist());
    }
}