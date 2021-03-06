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
    String albumName = "앨범";
    String trackName = "트랙";
    String trackArtist = "아티";

    @Before
    public void setUp() {
        albumSaveRequestDto = AlbumSaveRequestDto.builder().name(albumName).build();

        albumSaveRequestDto.getTracks().add(TrackSaveRequestDto.builder().name(trackName).artist(trackArtist).build());
        albumSaveRequestDto.getTracks().add(TrackSaveRequestDto.builder().name(trackName).artist(trackArtist).build());
        albumSaveRequestDto.getTracks().add(TrackSaveRequestDto.builder().name(trackName).artist(trackArtist).build());
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
        assertThat(savedTracks.get(0).getName()).isEqualTo(trackName);   //트랙 이름
        assertThat(savedTracks.get(0).getArtist()).isEqualTo(trackArtist);   //트랙 아티스트
    }

    @Test
    public void 앨범과_트랙이_수정된다() {
        //given
        Long albumId = albumRepository.save(albumSaveRequestDto.toEntity()).getId();
        Long trackId = trackRepository.findAll().get(0).getId();
        Long modTrackId = trackRepository.findAll().get(1).getId();
        Long rmTrackId = trackRepository.findAll().get(2).getId();

        String url = "/api/albums/" + albumId;

        AlbumUpdateRequestDto requestDto = AlbumUpdateRequestDto.builder().name("수정앨범").build();

        requestDto.getTracks().add(TrackUpdateRequestDto.builder().id(trackId).name("트랙").artist("아티").build());    //변경x
        requestDto.getTracks().add(TrackUpdateRequestDto.builder().id(modTrackId).name("수정트랙").artist("수정아티").state("U").build());  //수정
        requestDto.getTracks().add(TrackUpdateRequestDto.builder().id(rmTrackId).name("트랙").artist("아티").state("D").build());   //삭제
        requestDto.getTracks().add(TrackUpdateRequestDto.builder().name("추가트랙").artist("추가아티").state("I").build()); //추가

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
        assertThat(savedTracks.size()).isEqualTo(3);    //앨범을 가리키고있는 트랙들 수

        assertThat(savedTracks.get(0).getName()).isEqualTo("트랙"); //변경하지 않은 트랙 이름
        assertThat(savedTracks.get(0).getArtist()).isEqualTo("아티");   //변경하지 않은 트랙 아티스트

        assertThat(savedTracks.get(1).getName()).isEqualTo("수정트랙"); //변경한 트랙 이름
        assertThat(savedTracks.get(1).getArtist()).isEqualTo("수정아티");   //변경한 트랙 아티스트

        assertThat(savedTracks.get(2).getName()).isEqualTo("추가트랙"); //추가한 트랙 이름
        assertThat(savedTracks.get(2).getArtist()).isEqualTo("추가아티");   //추가한 트랙 아티스트
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