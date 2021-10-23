package com.brandpark.sharemusic.api.v1.album.query.dto;

import lombok.Data;

@Data
public class AlbumShortDto {
    private Long id;
    private String title;
    private String albumImage;
    private String description;
    private int trackCount;
    private String creator;
    private String creatorProfileImage;
}
