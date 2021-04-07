package com.brandpark.sharemusic.web.dto.tracks;

import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Getter
public class TrackSaveRequestDto implements Serializable {
    private String name;
    private String artist;

    @Builder
    public TrackSaveRequestDto(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public Track toEntity() {
        return Track.builder()
                .name(name)
                .artist(artist)
                .build();
    }
}
