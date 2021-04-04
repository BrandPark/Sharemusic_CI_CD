package com.brandpark.sharemusic.web.dto;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        Album album = Album.builder().name(name).build();

        for (TrackSaveRequestDto trackDto : tracks) {
            Track track = trackDto.toEntity();
            album.addTrack(track);
        }

        return album;
    }
}
