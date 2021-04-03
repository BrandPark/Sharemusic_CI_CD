package com.brandpark.sharemusic.web.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AlbumSaveRequestDtoTest {

    @Test
    public void Java_to_Json() throws JsonProcessingException {
        String expected = "{\"name\":\"앨범\",\"tracks\":[{\"name\":\"트랙0\",\"artist\":\"트랙0아티\",\"album\":null},{\"name\":\"트랙1\",\"artist\":\"트랙1아티\",\"album\":null}]}";
        List<TrackSaveRequestDto> tracks = new ArrayList<TrackSaveRequestDto>();
        tracks.add(TrackSaveRequestDto.builder().name("트랙0").artist("트랙0아티").build());
        tracks.add(TrackSaveRequestDto.builder().name("트랙1").artist("트랙1아티").build());
        AlbumSaveRequestDto requestDto = AlbumSaveRequestDto.builder()
                .name("앨범")
                .tracks(tracks)
                .build();
        ObjectMapper om = new ObjectMapper();
        String asString = om.writeValueAsString(requestDto);
        assertThat(asString).isEqualTo(expected);
    }

    @Test
    public void Json_to_Java() throws IOException {
        String expected = "{\"name\":\"앨범\",\"tracks\":[{\"name\":\"트랙0\",\"artist\":\"트랙0아티\",\"album\":null},{\"name\":\"트랙1\",\"artist\":\"트랙1아티\",\"album\":null}]}";
        String expected2 = "{\"name\":\"앨범\",\"tracks\":[{\"name\":\"트랙0\",\"artist\":\"트랙0아티\"},{\"name\":\"트랙1\",\"artist\":\"트랙1아티\"}]}";
        String expected3 = "{\"name\":\"앨범\",\"tracks\":[{\"name\":\"트랙0\",\"artist\":\"트랙0아티\"},{\"name\":\"트랙1\",\"artist\":\"트랙1아티\"}]}";
        ObjectMapper om = new ObjectMapper();

        AlbumSaveRequestDto requestDto = om.readValue(expected3, AlbumSaveRequestDto.class);
        List<TrackSaveRequestDto> list = requestDto.getTracks();
        assertThat(list.get(0).getName()).isEqualTo("트랙0");
    }
}