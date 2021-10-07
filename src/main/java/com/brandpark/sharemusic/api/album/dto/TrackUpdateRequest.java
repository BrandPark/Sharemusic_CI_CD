package com.brandpark.sharemusic.api.album.dto;

import lombok.Data;

@Data
public class TrackUpdateRequest {

    private Long id;

    private String name;

    private String artist;
}
