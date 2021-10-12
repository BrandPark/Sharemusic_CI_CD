package com.brandpark.sharemusic.api.album.query;

import lombok.Data;

@Data
public class AlbumShortDto {
    private String title;
    private String albumImage;
    private String description;
    private int trackCount;
    private String creator;
    private String creatorProfileImage;
}
