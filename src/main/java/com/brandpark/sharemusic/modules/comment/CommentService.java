package com.brandpark.sharemusic.modules.comment;

import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.modules.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long saveComment(Long accountId, Long albumId, String content) {

        Comment saveComment = commentRepository.save(Comment.createComment(accountId, albumId, content));

        eventPublisher.publishEvent(CommentEvent.builder()
                .writerId(accountId)
                .commentTargetAlbumId(albumId)
                .commentId(saveComment.getId())
                .build());

        return saveComment.getId();
    }
}
