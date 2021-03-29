package com.brandpark.sharemusic.domain.tracks;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TrackRepositoryTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    private TrackRepository trackRepository;

    List<Track> tracks = new ArrayList<Track>();
    Track track;
    Album album;

    @Before
    public void setUp() {
        album = Album.builder().name("album").tracks(tracks).build();
        track = Track.builder().name("트랙").artist("아티스트").album(album).build();
        tracks.add(track);
    }

    @After
    public void clearAll() {
        trackRepository.deleteAll();
    }

    @Test
    public void 트랙_저장_후_조회() {
        //given
        //when
        trackRepository.save(track);

        //then
        Track saved = trackRepository.findAll().get(0);
        assertThat(saved.getName()).isEqualTo("트랙");
        assertThat(saved.getArtist()).isEqualTo("아티스트");
        assertThat(saved.getAlbum()).isEqualTo(album);
    }

}