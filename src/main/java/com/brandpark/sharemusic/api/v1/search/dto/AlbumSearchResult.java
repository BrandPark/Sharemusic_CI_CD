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
    private Long trackCount;
    private List<TrackSearchResult> tracks;
    private Long accountId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Data
    public static class TrackSearchResult {
        private Long trackId;
        private String name;
        private String artist;
    }
}
