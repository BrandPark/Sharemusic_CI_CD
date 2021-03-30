package com.brandpark.sharemusic.web.dto;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.tracks.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TrackSaveRequestDto {
    private String name;
    private String artist;
    private Album album;

    @Builder
    public TrackSaveRequestDto(String name, String artist, Album album) {
        this.name = name;
        this.artist = artist;
        this.album = album;
    }

    public Track toEntity() {
        return Track.builder()
                .name(name)
                .album(album)
                .artist(artist)
                .build();
    }
}
