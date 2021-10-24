package com.brandpark.sharemusic.modules.comment;

import com.brandpark.sharemusic.modules.MyUtil;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Long saveComment(Long albumId, Long accountId, String content) {

        Comment comment = Comment.builder()
                .albumId(albumId)
                .accountId(accountId)
                .content(MyUtil.toBrTag(content))
                .build();

        return commentRepository.save(comment).getId();
    }
}
