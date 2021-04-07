package com.brandpark.sharemusic.domain;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.domain.tracks.TrackRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaTest {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    TrackRepository trackRepository;

    Track track0;
    Track track1;
    Album album0;
    Album album1;
    String[] albumNames = {"album1","album2"};
    String[] trackNames = {"track1", "track2"};
    String[] trackArtists = {"trackArtist1", "trackArtist2"};


    @Before
    public void setUp() {
        album0 = Album.builder().name(albumNames[0]).build();
        album1 = Album.builder().name(albumNames[0]).build();

        track0 = Track.builder().name(trackNames[0]).artist(trackArtists[0]).build();
        track1 = Track.builder().name(trackNames[1]).artist(trackArtists[1]).build();

        album0.addTrack(track0);
        album0.addTrack(track1);
        album1.addTrack(track0);
        album1.addTrack(track1);
    }

    @After
    public void clearAll() {
        albumRepository.deleteAll();
        trackRepository.deleteAll();
    }

    @Test
    public void 모든_앨범_조회() {
        //given
        albumRepository.save(album0);
        albumRepository.save(album1);

        //when
        List<Album> savedAlbums = albumRepository.findAllDesc();

        for (int i = 0; i < savedAlbums.size(); i++) {
            Album album = savedAlbums.get(i);
            List<Track> tracks = album.getTracks();
            assertThat(album.getName()).isEqualTo(albumNames[0]);
            assertThat(album.getTrackCount()).isEqualTo(2);

            assertThat(tracks.get(0).getName()).isEqualTo(trackNames[0]);
            assertThat(tracks.get(0).getArtist()).isEqualTo(trackArtists[0]);
            assertThat(tracks.get(1).getName()).isEqualTo(trackNames[1]);
            assertThat(tracks.get(1).getArtist()).isEqualTo(trackArtists[1]);
        }

    }

    @Test
    public void 앨범_저장_후_조회() {
        //given

        //when
        albumRepository.save(album0);

        //then
        Album savedAlbum = albumRepository.findAll().get(0);
        List<Track> savedTracks = savedAlbum.getTracks();

        assertThat(savedAlbum.getName()).isEqualTo(albumNames[0]);
        assertThat(savedTracks.size()).isEqualTo(savedAlbum.getTrackCount());

        assertThat(savedTracks.get(0).getName()).isEqualTo(trackNames[0]);
        assertThat(savedTracks.get(0).getArtist()).isEqualTo(trackArtists[0]);
        assertThat(savedTracks.get(1).getName()).isEqualTo(trackNames[1]);
        assertThat(savedTracks.get(1).getArtist()).isEqualTo(trackArtists[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 앨범_삭제_후_앨범_조회_예외() {
        //given
        albumRepository.save(album0);

        //when
        albumRepository.deleteById(album0.getId());

        //then
        albumRepository.findById(album0.getId()).orElseThrow(IllegalArgumentException::new);

    }

    @Test(expected = IllegalArgumentException.class)
    public void 앨범_삭제_후_귀속된_트랙_조회_예외() {
        //given
        albumRepository.save(album0);

        //when
        albumRepository.deleteById(album0.getId());

        //then
        trackRepository.findById(track1.getId()).orElseThrow(IllegalArgumentException::new);
    }

}