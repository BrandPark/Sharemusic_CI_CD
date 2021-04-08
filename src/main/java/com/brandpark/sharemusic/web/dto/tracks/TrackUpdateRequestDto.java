package com.brandpark.sharemusic.web.dto.tracks;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TrackUpdateRequestDto {
    private String name;
    private String artist;

    @Builder
    public TrackUpdateRequestDto(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }
    
}
