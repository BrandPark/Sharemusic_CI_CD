package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class AlbumServiceTest {

    @Autowired AlbumService albumService;
    @Autowired AlbumFactory albumFactory;

    @DisplayName("Album -> AlbumUpdateForm 필드 단순 삽입")
    @Test
    public void Album_To_AlbumUpdateForm() throws Exception {

        // given
        Album album = albumFactory.createAlbumWithTracks(5, 1L);

        // when
        AlbumUpdateForm form = albumService.entityToForm(album);

        // then
        assertThat(form.getTitle()).isEqualTo(album.getTitle());
        assertThat(form.getTracks().size()).isEqualTo(album.getTracks().size());
        for (int i = 0; i < form.getTracks().size(); i++) {
            assertThat(form.getTracks().get(i).getName()).isEqualTo(album.getTracks().get(i).getName());
            assertThat(form.getTracks().get(i).getArtist()).isEqualTo(album.getTracks().get(i).getArtist());
        }
    }
}