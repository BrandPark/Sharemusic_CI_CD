package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.Theme;
import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.album.AlbumRepository;
import com.brandpark.sharemusic.domain.album.Track;
import com.brandpark.sharemusic.web.api.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.api.dto.TrackSaveRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AlbumServiceTest {

    @Autowired
    AlbumService albumService;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    EntityManager em;

    @Test
    public void Album이_저장된다() throws Exception {
        // given
        List<TrackSaveRequestDto> trackDtos = new ArrayList<>(Collections.singletonList(createTrackDto()));

        Set<Theme> themes = new HashSet<>(Collections.singletonList(Theme.LOVE));

        AlbumSaveRequestDto albumDto = createAlbumDto(trackDtos, themes);
        albumDto.setTracks(trackDtos);

        // when
        Long saveId = albumService.saveAlbum(albumDto);
        persistToDb();

        // then
        Album findAlbum = albumRepository.findById(saveId).get();
        assertThat(findAlbum.getTitle()).isEqualTo(albumDto.getTitle());
        assertThat(findAlbum.getImgUrl()).isEqualTo(albumDto.getImgUrl());
        assertThat(findAlbum.getThemes()).isEqualTo(albumDto.getThemes());

        List<Track> findTracks = findAlbum.getTracks();
        assertThat(findTracks.size()).isEqualTo(trackDtos.size());
        assertThat(findTracks.get(0).getArtist()).isEqualTo(trackDtos.get(0).getArtist());
        assertThat(findTracks.get(0).getImgUrl()).isEqualTo(trackDtos.get(0).getImgUrl());
        assertThat(findTracks.get(0).getName()).isEqualTo(trackDtos.get(0).getName());
        assertThat(findTracks.get(0).getVideoUrl()).isEqualTo(trackDtos.get(0).getVideoUrl());
    }

    private void persistToDb() {
        em.flush();
        em.clear();
    }

    private TrackSaveRequestDto createTrackDto() {
        TrackSaveRequestDto trackDto = new TrackSaveRequestDto();
        trackDto.setName("track_name");
        trackDto.setArtist("artist");
        trackDto.setImgUrl("img_url");
        trackDto.setVideoUrl("video_url");
        return trackDto;
    }

    private AlbumSaveRequestDto createAlbumDto(List<TrackSaveRequestDto> tracks, Set<Theme> themes) {
        AlbumSaveRequestDto albumDto = new AlbumSaveRequestDto();
        albumDto.setTracks(tracks);
        albumDto.setThemes(themes);
        albumDto.setTitle("album_name");
        albumDto.setImgUrl("img_url");

        return albumDto;
    }


}