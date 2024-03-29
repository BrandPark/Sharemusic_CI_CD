package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Comment;
import com.brandpark.sharemusic.modules.album.domain.CommentRepository;
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
    private final AlbumRepository albumRepository;

    @Transactional
    public Long saveComment(Long accountId, Long albumId, String content) {

        Comment comment = commentRepository.save(Comment.createComment(accountId, albumId, content));

        Album album = albumRepository.findById(albumId).get();

        if (isNotMyAlbum(accountId, album)) {
            eventPublisher.publishEvent(CommentEvent.createCommentEvent(accountId, albumId, comment.getId()));
        }

        return comment.getId();
    }

    private boolean isNotMyAlbum(Long accountId, Album album) {
        return !album.getAccountId().equals(accountId);
    }
}
