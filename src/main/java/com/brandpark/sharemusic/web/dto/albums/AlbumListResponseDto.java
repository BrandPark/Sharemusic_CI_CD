package com.brandpark.sharemusic.web.dto.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AlbumListResponseDto {
    private Long id;
    private String name;
    private int trackCount;
    private LocalDateTime modifiedDate;

    public AlbumListResponseDto(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        this.trackCount = album.getTrackCount();
        this.modifiedDate = album.getModifiedDate();
    }
}
