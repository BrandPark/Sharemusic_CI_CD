package com.brandpark.sharemusic.modules.comment.domain;

public interface ExtendCommentRepository {
    void deleteAllCommentsByAlbumId(Long albumId);
}
