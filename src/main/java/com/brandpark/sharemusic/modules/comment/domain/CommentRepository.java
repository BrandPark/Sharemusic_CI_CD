package com.brandpark.sharemusic.modules.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long>, ExtendCommentRepository {
    Page<Comment> findAllByAlbumId(Pageable pageable, Long albumId);

    boolean existsByIdAndAlbumId(Long commentId, Long albumId);

    @Override
    void deleteAllCommentsByAlbumId(Long albumId);
}
