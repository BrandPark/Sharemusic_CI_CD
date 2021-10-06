package com.brandpark.sharemusic.modules.album.form;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlbumUpdateForm {

    private String title;

    private List<TrackUpdateForm> tracks = new ArrayList<>();
}
