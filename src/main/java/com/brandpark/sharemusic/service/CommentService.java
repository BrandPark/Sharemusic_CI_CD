package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.album.AlbumRepository;
import com.brandpark.sharemusic.domain.comment.Comment;
import com.brandpark.sharemusic.domain.comment.CommentRepository;
import com.brandpark.sharemusic.domain.user.User;
import com.brandpark.sharemusic.domain.user.UserRepository;
import com.brandpark.sharemusic.exception.NotFoundAlbumException;
import com.brandpark.sharemusic.exception.NotFoundCommentException;
import com.brandpark.sharemusic.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final AlbumRepository albumRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveComment(Long albumId, Long userId, String contents) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundAlbumException("존재하지 않는 앨범입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않는 사용자입니다."));

        Comment comment = Comment.createComment(album, user, contents);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(Long commentId, String contents) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("존재하지 않는 댓글입니다."));

        comment.update(contents);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException("존재하지 않는 댓글입니다."));

        commentRepository.delete(comment);
    }
}
