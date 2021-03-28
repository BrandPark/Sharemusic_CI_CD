package com.brandpark.sharemusic.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AlbumUpdateRequestDto {
    private String name;

    @Builder
    public AlbumUpdateRequestDto(String name) {
        this.name = name;
    }
}
