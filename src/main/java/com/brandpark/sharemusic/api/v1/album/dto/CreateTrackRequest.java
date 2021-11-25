package com.brandpark.sharemusic.api.v1.album.dto;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(of = {"name", "artist"})
@Data
public class CreateTrackRequest {

    @NotBlank(message = "트랙 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "트랙 아티스트를 입력해 주세요.")
    private String artist;

    public Track toEntity(Album album) {
        return Track.builder()
                .name(name)
                .artist(artist)
                .album(album)
                .build();
    }
}
