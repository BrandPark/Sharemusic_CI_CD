package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.modules.album.domain.Comment;
import com.brandpark.sharemusic.modules.album.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Profile("test")
@Component
public class CommentFactory {

    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    public Comment persistComment(String content, Long accountId, Long albumId) {
        Comment comment = commentRepository.save(createComment(content, accountId, albumId));

        entityManager.flush();
        entityManager.clear();

        return comment;
    }

    public List<Comment> persistComments(String content, Long accountId, Long albumId, int count) {

        List<Comment> savedComments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            savedComments.add(createComment(content + i, accountId, albumId));
        }

        commentRepository.saveAll(savedComments);
        entityManager.flush();
        entityManager.clear();

        return savedComments;
    }

    private Comment createComment(String content, Long accountId, Long albumId) {
        return Comment.createComment(accountId, albumId, content);
    }
}
