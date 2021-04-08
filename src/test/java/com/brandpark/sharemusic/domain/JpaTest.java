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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTest {
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
    public void 앨범과_트랙저장() {
        //given
        albumRepository.save(album1);

        //when
        Album savedAlbum = albumRepository.findById(album1.getId()).orElseThrow(IllegalArgumentException::new);
        List<Track> savedTracks = trackRepository.findAllWithAlbum(savedAlbum);

        //then
        //---앨범 검사
        assertThat(savedAlbum.getName()).isEqualTo("앨범1");  //이름
        assertThat(savedAlbum.getTrackCount()).isEqualTo(2);    //트랙 수

        //---트랙 검사
        assertThat(savedTracks.size()).isEqualTo(2);    //트랙 수
        assertThat(savedTracks.get(0).getName()).isEqualTo("트랙1");  //이름
        assertThat(savedTracks.get(0).getArtist()).isEqualTo("아티1");    //아티스트
    }

    @Test
    public void 모든_앨범_변경날짜_순서로_조회() {
        //given
        albumRepository.save(album1);
        albumRepository.save(album2);

        //when
        List<Album> albumList = albumRepository.findAllDesc();

        //then
        assertThat(albumList.get(0).getName()).isEqualTo("앨범2");    //나중에 추가한 앨범이 먼저나온다.
        assertThat(albumList.get(0).getModifiedDate()).isAfter(albumList.get(1).getModifiedDate()); //기준이 되는 수정 시간
    }

    @Test(expected = IllegalArgumentException.class)
    public void 삭제된_앨범_조회_예외() {
        //given
        albumRepository.save(album1);

        //when
        albumRepository.deleteById(album1.getId());

        //then
        albumRepository.findById(album1.getId()).orElseThrow(IllegalArgumentException::new);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 삭제된_앨범의_트랙_조회_예외() {
        //given
        albumRepository.save(album1);

        //when
        albumRepository.deleteById(album1.getId());

        //then
        trackRepository.findById(album1.getTracks().get(0).getId()).orElseThrow(IllegalArgumentException::new);
    }

}