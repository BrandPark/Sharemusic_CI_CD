package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.modules.album.domain.Album;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumSaveRequestTest {

    AlbumFactory albumFactory = new AlbumFactory();

    @DisplayName("AlbumSaveRequest -> Album-Track 도메인 생성")
    @Test
    void toEntity() {

        // given
        AlbumSaveRequest dto = albumFactory.createAlbumSaveDto();
        Long accountId = 1L;

        // when
        Album album = dto.toEntity(1L);

        // then
        assertThat(album.getTitle()).isEqualTo(dto.getTitle());
        assertThat(album.getAccountId()).isEqualTo(accountId);
        assertThat(album.getTracks().size()).isEqualTo(dto.getTracks().size());

        for (int i = 0; i < album.getTracks().size(); i++) {
            assertThat(album.getTracks().get(i).getAlbum()).isEqualTo(album);
            assertThat(album.getTracks().get(i).getName()).isEqualTo(dto.getTracks().get(i).getName());
            assertThat(album.getTracks().get(i).getArtist()).isEqualTo(dto.getTracks().get(i).getArtist());
        }
    }
}