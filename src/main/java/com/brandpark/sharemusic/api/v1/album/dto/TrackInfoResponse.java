package com.brandpark.sharemusic.api.v1.album.dto;

import lombok.Data;

@Data
public class TrackInfoResponse {
    private Long trackId;
    private String name;
    private String artist;
}
