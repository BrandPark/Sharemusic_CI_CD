package com.brandpark.sharemusic.modules.album.dto;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.Data;

@Data
public class TrackSaveDto {

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
