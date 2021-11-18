package com.brandpark.sharemusic.api.v1.album.query.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumShortDto {
    private Long albumId;
    private String title;
    private String albumImage;
    private String description;
    private int trackCount;
    private String creatorNickname;
    private String creatorProfileImage;
    private LocalDateTime createdDate;
}
