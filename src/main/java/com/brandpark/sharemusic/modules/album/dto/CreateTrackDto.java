package com.brandpark.sharemusic.modules.album.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateTrackDto {
    private String name;
    private String artist;
}
