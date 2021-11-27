package com.brandpark.sharemusic.modules.comment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByAlbumId(Pageable pageable, Long albumId);

    void deleteByAlbumId(Long albumId);
}
