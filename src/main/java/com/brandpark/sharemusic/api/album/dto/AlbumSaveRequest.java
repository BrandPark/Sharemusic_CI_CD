package com.brandpark.sharemusic.api.album.dto;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class AlbumSaveRequest {

    private String title;

    private String bio;

    private String albumImage;

    private List<TrackSaveRequest> tracks = new ArrayList<>();

    public Album toEntity(Long accountId) {
        Album album = Album.builder()
                .accountId(accountId)
                .title(title)
                .bio(bio)
                .albumImage(albumImage)
                .build();

        List<Track> trackEntities = tracks.stream()
                .map(t -> t.toEntity(album))
                .collect(toList());

        album.getTracks().addAll(trackEntities);

        return album;
    }
}
