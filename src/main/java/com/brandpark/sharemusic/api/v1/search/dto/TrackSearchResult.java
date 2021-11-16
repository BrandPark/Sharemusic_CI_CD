package com.brandpark.sharemusic.api.v1.search.dto;

import lombok.Data;

@Data
public class TrackSearchResult {
    private Long trackId;
    private String name;
    private String artist;
}
