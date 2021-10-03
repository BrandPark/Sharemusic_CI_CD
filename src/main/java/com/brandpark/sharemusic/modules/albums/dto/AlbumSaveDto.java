package com.brandpark.sharemusic.modules.albums.dto;

import com.brandpark.sharemusic.modules.albums.domain.Album;
import com.brandpark.sharemusic.modules.albums.domain.Track;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class AlbumSaveDto {

    private String title;

    private List<TrackSaveDto> tracks = new ArrayList<>();

    public Album toEntity(Long accountId) {
        Album album = Album.builder()
                .accountId(accountId)
                .title(title)
                .build();

        List<Track> trackEntities = tracks.stream()
                .map(t -> t.toEntity(album))
                .collect(toList());

        album.getTracks().addAll(trackEntities);

        return album;
    }
}
