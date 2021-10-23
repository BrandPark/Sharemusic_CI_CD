package com.brandpark.sharemusic.api.v1.album.query.dto;

import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
}
