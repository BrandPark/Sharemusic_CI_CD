package com.brandpark.sharemusic.api.v1.album.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumInfoResponse {
    private Long albumId;
    private String title;
    private String albumImage;
    private String description;
    private int trackCount;
    private List<TrackInfoResponse> tracks;
    private Long accountId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Data
    public static class TrackInfoResponse {
        private Long trackId;
        private String name;
        private String artist;
    }
}
