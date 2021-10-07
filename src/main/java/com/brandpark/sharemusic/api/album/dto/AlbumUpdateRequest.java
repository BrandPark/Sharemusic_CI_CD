package com.brandpark.sharemusic.api.album.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlbumUpdateRequest {

    private String title;

    private String bio;

    private String albumImage;

    private List<TrackUpdateRequest> tracks;
}
