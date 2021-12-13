package com.brandpark.sharemusic.modules.comment.domain.repoImpl;

import com.brandpark.sharemusic.modules.comment.domain.ExtendCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements ExtendCommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteAllCommentsByAlbumId(Long albumId) {
        jdbcTemplate.update("delete from comment where album_id=?", new Object[] {albumId});
    }
}
