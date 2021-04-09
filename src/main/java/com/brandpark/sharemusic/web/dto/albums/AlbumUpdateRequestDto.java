package com.brandpark.sharemusic.web.dto.albums;

import com.brandpark.sharemusic.web.dto.tracks.TrackUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class AlbumUpdateRequestDto {
    private String name;
    private List<TrackUpdateRequestDto> tracks = new ArrayList<>();

    @Builder
    public AlbumUpdateRequestDto(String name, List<TrackUpdateRequestDto> tracks) {
        this.name = name;
        if(tracks != null)
            this.tracks = tracks;
    }
}
