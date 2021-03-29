package com.brandpark.sharemusic.domain.albums;

import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.domain.tracks.TrackRepository;
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
public class AlbumRepositoryTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    List<Track> tracks = new ArrayList<Track>();
    Track track1;
    Track track2;
    Album album;

    @Before
    public void setUp() {
        album = Album.builder().name("album").tracks(tracks).build();
        track1 = Track.builder().name("노래1").artist("아티스트1").album(album).build();
        track2 = Track.builder().name("노래2").artist("아티스트2").album(album).build();
        tracks.add(track1);
        tracks.add(track2);
    }

    @After
    public void clearAll() {
        albumRepository.deleteAll();
        trackRepository.deleteAll();
    }

    @Test
    public void 앨범_저장_후_조회() {
        //given

        //when
        albumRepository.save(album);

        //then
        Album saved = albumRepository.findAll().get(0);
        List<Track> savedTracks = saved.getTracks();

        assertThat(saved.getName()).isEqualTo("album");
        assertThat(savedTracks.get(0).getName()).isEqualTo("노래1");
        assertThat(savedTracks.get(0).getArtist()).isEqualTo("아티스트1");
        assertThat(savedTracks.get(1).getName()).isEqualTo("노래2");
        assertThat(savedTracks.get(1).getArtist()).isEqualTo("아티스트2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void 앨범_삭제_후_앨범_조회_예외() {
        //given
        albumRepository.save(album);

        //when
        albumRepository.deleteById(album.getId());

        //then
        albumRepository.findById(album.getId()).orElseThrow(IllegalArgumentException::new);

    }

    @Test(expected = IllegalArgumentException.class)
    public void 앨범_삭제_후_귀속된_트랙_조회_예외() {
        //given
        albumRepository.save(album);

        //when
        albumRepository.deleteById(album.getId());

        //then
        trackRepository.findById(track1.getId()).orElseThrow(IllegalArgumentException::new);
    }

}