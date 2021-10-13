package com.brandpark.sharemusic.modules.album.form;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlbumUpdateForm {

    private Long id;

    private String title;

    private String description;

    private String albumImage;

    private List<TrackUpdateForm> tracks = new ArrayList<>();
}
