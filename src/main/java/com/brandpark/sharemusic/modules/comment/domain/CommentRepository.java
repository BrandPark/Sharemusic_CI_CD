package com.brandpark.sharemusic.modules.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAlbumId(Long albumId);
}
