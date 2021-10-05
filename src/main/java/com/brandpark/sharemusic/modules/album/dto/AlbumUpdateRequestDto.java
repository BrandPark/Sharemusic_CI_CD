package com.brandpark.sharemusic.modules.album.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlbumUpdateRequestDto {

    private String title;

    private List<TrackUpdateRequestDto> tracks = new ArrayList<>();
}
