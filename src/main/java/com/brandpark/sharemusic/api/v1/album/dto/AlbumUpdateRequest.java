package com.brandpark.sharemusic.api.v1.album.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlbumUpdateRequest {

    private String title;

    private String description;

    private String albumImage;

    private List<TrackUpdateRequest> tracks;
}
