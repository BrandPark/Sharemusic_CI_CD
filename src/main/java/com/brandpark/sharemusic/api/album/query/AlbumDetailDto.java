package com.brandpark.sharemusic.api.album.query;

import lombok.Data;

import java.util.List;

@Data
public class AlbumDetailDto {
    private Long id;
    private String title;
    private String albumImage;
    private String description;
    private List<TrackDetailDto> tracks;
    private String creator;
    private String creatorProfileImage;
}
