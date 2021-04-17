package com.brandpark.sharemusic.web.dto.tracks;

import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TrackUpdateRequestDto {
    private Long id;
    private String name;
    private String artist;
    private String state;

    @Builder
    public TrackUpdateRequestDto(Long id, String name, String artist, String state) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.state = state;
    }

    public Track toEntity() {
        return Track.builder()
                .id(id)
                .name(name)
                .artist(artist)
                .build();
    }
}
