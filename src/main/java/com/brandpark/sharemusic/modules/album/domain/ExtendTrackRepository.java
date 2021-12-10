package com.brandpark.sharemusic.modules.album.domain;

import java.util.List;

public interface ExtendTrackRepository {
    int batchInsert(List<Track> tracks, Long albumId);

    int batchUpdate(List<Track> tracks);

    int batchDelete(List<Track> tracks);
}
