package com.brandpark.sharemusic.web.api.dto;

import com.brandpark.sharemusic.domain.album.Track;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrackSaveRequestDto {
    private String name;
    private String artist;
    private String imgUrl;
    private String videoUrl;

    public Track toEntity() {
        return Track.builder()
                .name(name)
                .artist(artist)
                .imgUrl(imgUrl)
                .videoUrl(videoUrl)
                .build();
    }
}
