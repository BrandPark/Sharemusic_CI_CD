package com.brandpark.sharemusic.api.album.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"name", "artist"})
@Data
public class TrackUpdateRequest {

    private Long id;

    private String name;

    private String artist;
}
