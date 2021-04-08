package com.brandpark.sharemusic.web.dto.albums;

import com.brandpark.sharemusic.web.dto.tracks.TrackUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AlbumUpdateRequestDto {
    private String name;
    private List<TrackUpdateRequestDto> tracks;

    @Builder
    public AlbumUpdateRequestDto(String name, List<TrackUpdateRequestDto> tracks) {
        this.name = name;
        this.tracks = tracks;
    }
}
