package com.brandpark.sharemusic.web.dto.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.web.dto.tracks.TrackResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class AlbumResponseDto {
    private Long id;
    private String name;
    private List<TrackResponseDto> tracks;
    private int trackCount;
    private LocalDateTime modifiedDate;

    public AlbumResponseDto(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.tracks = album.getTracks().stream().map(TrackResponseDto::new).collect(Collectors.toList());
        this.trackCount = album.getTrackCount();
        this.modifiedDate = album.getModifiedDate();
    }
}
