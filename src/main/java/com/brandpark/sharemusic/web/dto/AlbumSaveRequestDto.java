package com.brandpark.sharemusic.web.dto;

import com.brandpark.sharemusic.domain.albums.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AlbumSaveRequestDto {
    private String name;

    @Builder
    public AlbumSaveRequestDto(String name) {
        this.name = name;
    }

    public Album toEntity() {
        return Album.builder()
                .name(name)
                .build();
    }
}
