package com.brandpark.sharemusic.web.dto.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.web.dto.tracks.TrackSaveRequestDto;
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
        if(tracks != null)
            this.tracks = tracks;
    }

    public Album toEntity() {
        Album album = Album.builder().name(name).build();

        tracks.forEach(track -> album.addTrack(track.toEntity()));
//        for (TrackSaveRequestDto trackDto : tracks) {
//            album.addTrack(trackDto.toEntity());
//        }

        return album;
    }
}
