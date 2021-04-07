package com.brandpark.sharemusic.service.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.domain.tracks.TrackRepository;
import com.brandpark.sharemusic.web.dto.albums.AlbumListResponseDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlbumApiServiceTest {

    @Autowired
    private AlbumApiService service;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    Track track0;
    Track track1;
    Track track2;
    Track track3;
    Album album0;
    Album album1;
    String[] albumNames = {"album1", "album2"};
    String[] trackNames = {"track1", "track2"};
    String[] trackArtists = {"trackArtist1", "trackArtist2"};

    @Before
    public void setUp() {
        album0 = Album.builder().name(albumNames[0]).build();
        album1 = Album.builder().name(albumNames[1]).build();

        track0 = Track.builder().name(trackNames[0]).artist(trackArtists[0]).build();
        track1 = Track.builder().name(trackNames[1]).artist(trackArtists[1]).build();
        track2 = Track.builder().name(trackNames[0]).artist(trackArtists[0]).build();
        track3 = Track.builder().name(trackNames[1]).artist(trackArtists[1]).build();

        album0.addTrack(track0);
        album0.addTrack(track1);
        album1.addTrack(track2);
        album1.addTrack(track3);

        albumRepository.save(album0);
        albumRepository.save(album1);
    }

    @After
    public void clearAll() {
        albumRepository.deleteAll();
        trackRepository.deleteAll();
    }

    @Test
    public void 모든_앨범_최신순_조회() {
        //given

        //when
        List<AlbumListResponseDto> albumList = service.findAllDesc();

        //then
        AlbumListResponseDto firstDto = albumList.get(0);
        AlbumListResponseDto secondDto = albumList.get(1);

        assertThat(firstDto.getName()).isEqualTo(albumNames[1]);
        assertThat(firstDto.getTrackCount()).isEqualTo(2);

        assertThat(secondDto.getName()).isEqualTo(albumNames[0]);
        assertThat(secondDto.getTrackCount()).isEqualTo(2);
    }
}