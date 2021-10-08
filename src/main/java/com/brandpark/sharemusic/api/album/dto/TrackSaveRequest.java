package com.brandpark.sharemusic.api.album.dto;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"name", "artist"})
@Data
public class TrackSaveRequest {

    private String name;
    private String artist;

    public Track toEntity(Album album) {
        return Track.builder()
                .name(name)
                .artist(artist)
                .album(album)
                .build();
    }
}
