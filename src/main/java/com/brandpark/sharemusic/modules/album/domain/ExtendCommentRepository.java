package com.brandpark.sharemusic.modules.album.domain;

public interface ExtendCommentRepository {
    void deleteAllCommentsByAlbumId(Long albumId);
}
