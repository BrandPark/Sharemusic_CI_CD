package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.web.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.AlbumUpdateRequestDto;
import org.junit.After;
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
    public void tearDown() {
        albumRepository.deleteAll();
    }

    @Test
    public void 앨범이_저장된다() {
        //given
        String name = "album1";
        AlbumSaveRequestDto requestDto = AlbumSaveRequestDto.builder().name(name).build();
        String url = "http://localhost:" + port + "/api/albums";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        Album saved = albumRepository.findAll().get(0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(saved.getName()).isEqualTo(name);
    }

    @Test
    public void 앨범이_수정된다() {
        //given
        Album saved = albumRepository.save(Album.builder().name("mingon").build());
        String expectedName = "MINGON";
        String url = "http://localhost:" + port + "/api/albums/" + saved.getId();

        AlbumUpdateRequestDto requestDto = AlbumUpdateRequestDto.builder().name(expectedName).build();
        HttpEntity<AlbumUpdateRequestDto> httpEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Long.class);
        Album modified = albumRepository.findById(saved.getId()).orElseThrow(IllegalArgumentException::new);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(modified.getName()).isEqualTo(expectedName);
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
    }0
}