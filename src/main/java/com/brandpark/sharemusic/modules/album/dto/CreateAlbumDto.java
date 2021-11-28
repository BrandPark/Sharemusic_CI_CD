package com.brandpark.sharemusic.modules.album.dto;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Data
public class CreateAlbumDto {

    private String title;

    private String description;

    private String albumImage;

    private List<CreateTrackDto> tracks;

    public Album toEntity(Long accountId) {
        Album album = Album.createAlbum(title, description, albumImage, accountId);

        List<Track> tracks = this.tracks.stream()
                .map(t -> Track.createTrack(t.getName(), t.getArtist()))
                .collect(toList());

        tracks.stream().forEach(t -> t.initAlbum(album));

        album.addAllTrack(tracks);

        return album;
    }

    @AllArgsConstructor
    @Data
    public static class CreateTrackDto {
        private String name;
        private String artist;
    }
}
