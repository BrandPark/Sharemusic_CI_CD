package com.brandpark.sharemusic.modules.album.domain.repoImpl;

import com.brandpark.sharemusic.modules.album.domain.ExtendCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class ExtendCommentRepositoryImpl implements ExtendCommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteAllCommentsByAlbumId(Long albumId) {
        jdbcTemplate.update("DELETE FROM COMMENT WHERE ALBUM_ID=?", new Object[] {albumId});
    }
}
