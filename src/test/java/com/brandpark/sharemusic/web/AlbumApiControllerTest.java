package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.domain.tracks.TrackRepository;
import com.brandpark.sharemusic.web.dto.albums.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.albums.AlbumUpdateRequestDto;
import com.brandpark.sharemusic.web.dto.tracks.TrackSaveRequestDto;
import com.brandpark.sharemusic.web.dto.tracks.TrackUpdateRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlbumApiControllerTest {

    @LocalServerPort String port;
    @Autowired TestRestTemplate restTemplate;

    @Autowired AlbumRepository albumRepository;
    @Autowired TrackRepository trackRepository;

    AlbumSaveRequestDto albumSaveRequestDto;
    TrackSaveRequestDto trackSaveRequestDto;

    @Before
    public void setUp() {
        albumSaveRequestDto = AlbumSaveRequestDto.builder().name("앨범").build();
        trackSaveRequestDto = TrackSaveRequestDto.builder().name("트랙").artist("아티").build();
        albumSaveRequestDto.getTracks().add(trackSaveRequestDto);
    }

    @After
    public void clearAll() {
        albumRepository.deleteAll();
    }

    @Test
    public void 앨범과_트랙이_저장된다() {
        //given
        String url = "http://localhost:" + port + "/api/albums";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, albumSaveRequestDto, Long.class);

        //then
        Album savedAlbum = albumRepository.findAll().get(0);
        List<Track> savedTracks = trackRepository.findAllWithAlbum(savedAlbum);

        //---응답
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);    // 응답상태
        assertThat(responseEntity.getBody()).isGreaterThan(0L); //응답 바디
        //---앨범
        assertThat(savedAlbum.getName()).isEqualTo(albumSaveRequestDto.getName());   //앨범이름
        assertThat(savedAlbum.getTrackCount()).isEqualTo(savedTracks.size());    //앨범에 저장된 트랙 수
        //---트랙
        assertThat(savedTracks.get(0).getName()).isEqualTo(trackSaveRequestDto.getName());   //트랙 이름
        assertThat(savedTracks.get(0).getArtist()).isEqualTo(trackSaveRequestDto.getArtist());   //트랙 아티스트
    }

    @Test
    public void 앨범과_트랙이_수정된다() {
        //given
        Long albumId = albumRepository.save(albumSaveRequestDto.toEntity()).getId();
        Long trackId = trackRepository.findAll().get(0).getId();

        String url = "/api/albums/" + albumId;

        AlbumUpdateRequestDto requestDto = AlbumUpdateRequestDto.builder().name("수정앨범").build();
        requestDto.getTracks().add(TrackUpdateRequestDto.builder().id(trackId).name("수정트랙").artist("수정아티").build());

        HttpEntity<AlbumUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity
                = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        Album savedAlbum = albumRepository.findById(albumId).orElseThrow(IllegalArgumentException::new);
        List<Track> savedTracks = trackRepository.findAllWithAlbum(savedAlbum);
        //---응답
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);    //응답 상태
        assertThat(responseEntity.getBody()).isGreaterThan(0L); //응답 바디
        //---앨범
        assertThat(savedAlbum.getName()).isEqualTo("수정앨범"); //앨범 이름
        //---트랙
        assertThat(savedTracks.size()).isEqualTo(1);    //앨범을 가리키고있는 트랙들 수
        assertThat(savedTracks.get(0).getName()).isEqualTo("수정트랙"); //트랙 이름
        assertThat(savedTracks.get(0).getArtist()).isEqualTo("수정아티");   //트랙 아티스트
    }

    @Test(expected=IllegalArgumentException.class)
    public void 삭제된_앨범은_조회되지_않는다() {
        //given
        Long albumId = albumRepository.save(Album.builder().name("mingon").build()).getId();
        String url = "http://localhost:" + port + "/api/albums/" + albumId;

        //when
        restTemplate.delete(url);

        //then
        Album deleted = albumRepository.findById(albumId).orElseThrow(IllegalArgumentException::new);
    }
}