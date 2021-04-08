package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.domain.tracks.Track;
import com.brandpark.sharemusic.web.dto.albums.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.tracks.TrackSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlbumApiControllerTest {

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlbumRepository albumRepository;

    @After
    public void clearAll() {
        albumRepository.deleteAll();
    }

    @Test
    public void 앨범이_저장된다() {
        //given
        String name = "앨범";
        List<TrackSaveRequestDto> tracks = new ArrayList<>();
        TrackSaveRequestDto track = TrackSaveRequestDto.builder().name("트랙").artist("아티스트").build();
        AlbumSaveRequestDto requestDto = AlbumSaveRequestDto.builder().name(name).tracks(tracks).build();
        String url = "http://localhost:" + port + "/api/albums";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        Album savedAlbum = albumRepository.findAll().get(0);
        List<Track> savedTracks = savedAlbum.getTracks();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);    // 상태확인
        assertThat(savedAlbum.getName()).isEqualTo(name);   //앨범이름 확인
        assertThat(savedTracks.size()).isEqualTo(savedAlbum.getTrackCount());    //앨범에 저장된 트랙 수 확인
        assertThat(savedTracks.get(0).getName()).isEqualTo("트랙");   //트랙 이름 확인
        assertThat(savedTracks.get(0).getArtist()).isEqualTo("아티스트");   //트랙 아티스트 확인

    }

    @Test
    public void 앨범이_수정된다() {
        //given

    }

    @Test(expected=IllegalArgumentException.class)
    public void 앨범이_삭제된다() {
        //given
        Album saved = albumRepository.save(Album.builder().name("mingon").build());
        String url = "http://localhost:" + port + "/api/albums/" + saved.getId();

        //when
        restTemplate.delete(url);

        //then
        Album deleted = albumRepository.findById(saved.getId()).orElseThrow(IllegalArgumentException::new);
    }
}