package com.brandpark.sharemusic.modules.album.domain;

import java.util.List;

public interface ExtendTrackRepository {
    int batchInsert(List<Track> tracks);

    int batchUpdate(List<Track> tracks);
}
