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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlbumApiServiceTest {

    @Autowired
    private AlbumApiService albumApiservice;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    Album album1;
    Album album2;

    @Before
    public void setUp() {
        album1 = Album.builder().name("앨범1").tracks(new ArrayList<>()).build();
        album1.addTrack(Track.builder().name("트랙1").artist("아티1").album(album1).build());
        album1.addTrack(Track.builder().name("트랙2").artist("아티2").album(album1).build());

        album2 = Album.builder().name("앨범2").tracks(new ArrayList<>()).build();
        album2.addTrack(Track.builder().name("트랙1").artist("아티1").album(album2).build());
        album2.addTrack(Track.builder().name("트랙2").artist("아티2").album(album2).build());
    }

    @After
    public void clearAll() {
        albumRepository.deleteAll();
        trackRepository.deleteAll();
    }

    @Test
    public void 모든_앨범_최신순_조회() {
        //given
        albumRepository.save(album1);
        albumRepository.save(album2);

        //when
        List<AlbumListResponseDto> albumList = albumApiservice.findAllDesc();

        //then
        assertThat(albumList.get(0).getName()).isEqualTo("앨범2");
        assertThat(albumList.get(0).getModifiedDate()).isAfter(albumList.get(1).getModifiedDate());
    }
}