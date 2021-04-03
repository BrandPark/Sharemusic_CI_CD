package com.brandpark.sharemusic.web.dto;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class AlbumSaveRequestDto implements Serializable {
    private String name;
    private List<TrackSaveRequestDto> tracks = new ArrayList<>();

    @Builder
    public AlbumSaveRequestDto(String name, List<TrackSaveRequestDto> tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public Album toEntity() {
        List<Track> list2 = tracks.stream().map((t) -> t.toEntity()).collect(Collectors.toList());
        return Album.builder()
                .name(name)
                .tracks(list2)
                .build();
    }
}
