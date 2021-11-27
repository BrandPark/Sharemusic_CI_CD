package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
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

    public List<Comment> persistComments(String content, Long accountId, Long albumId, int count) {

        List<Comment> savedComments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            savedComments.add(Comment.createComment(accountId, albumId, content + i));
        }

        commentRepository.saveAll(savedComments);
        entityManager.flush();
        entityManager.clear();

        return savedComments;
    }
}
