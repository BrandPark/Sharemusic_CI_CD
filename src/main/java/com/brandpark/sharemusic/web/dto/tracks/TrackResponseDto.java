package com.brandpark.sharemusic.web.dto.tracks;

import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TrackResponseDto {
    private Long id;
    private String name;
    private String artist;

    public TrackResponseDto(Track track) {
        this.id = track.getId();
        this.name = track.getName();
        this.artist = track.getArtist();
    }
}
