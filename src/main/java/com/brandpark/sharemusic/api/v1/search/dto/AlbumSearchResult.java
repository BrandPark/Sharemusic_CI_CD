package com.brandpark.sharemusic.api.v1.search.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumSearchResult {
    private Long albumId;
    private String title;
    private String description;
    private String albumImage;
    private int trackCount;
    private String creatorNickname;
    private String creatorProfileImage;
    private List<TrackSearchResult> tracks;
    private LocalDateTime createdDate;
}
