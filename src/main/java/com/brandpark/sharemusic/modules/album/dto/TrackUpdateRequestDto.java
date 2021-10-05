package com.brandpark.sharemusic.modules.album.dto;

import lombok.Data;

@Data
public class TrackUpdateRequestDto {

    private Long id;
    private String name;
    private String artist;

}
