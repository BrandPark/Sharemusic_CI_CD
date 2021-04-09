package com.brandpark.sharemusic.web.dto.tracks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TrackUpdateRequestDto {
    private Long id;
    private String name;
    private String artist;

    @Builder
    public TrackUpdateRequestDto(Long id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }
}
