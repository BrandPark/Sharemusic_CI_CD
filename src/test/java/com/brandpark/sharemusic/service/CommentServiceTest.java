package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.comment.Comment;
import com.brandpark.sharemusic.domain.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired CommentService commentService;
    @Autowired EntityManager em;

    @Test
    public void 댓글이_저장된다() throws Exception {
        // given
        User user = createUser();
        Album album = createAlbum();

        String contents = "contents";

        // when
        Long saveId = commentService.saveComment(album.getId(), user.getId(), contents);
        persistToDb();

        // then
        Comment findComment = em.find(Comment.class, saveId);
        assertThat(findComment.getAlbum().getId()).isEqualTo(album.getId());
        assertThat(findComment.getUser().getId()).isEqualTo(user.getId());
        assertThat(findComment.getContents()).isEqualTo(contents);
    }

    @Test
    public void 댓글이_수정된다() throws Exception {
        // given
        User user = createUser();
        Album album = createAlbum();
        Comment comment = createComment(user, album);

        persistToDb();

        // when
        String modContents = "mod_contents";
        commentService.updateComment(comment.getId(), modContents);
        persistToDb();

        // then
        Comment findComment = em.find(Comment.class, comment.getId());
        assertThat(findComment.getContents()).isEqualTo(modContents);
    }

    @Test
    public void 댓글이_삭제된다() throws Exception {
        // given
        User user = createUser();
        Album album = createAlbum();
        Comment comment = createComment(user, album);

        persistToDb();

        // when
        commentService.deleteComment(comment.getId());
        persistToDb();

        // then
        Comment findComment = em.find(Comment.class, comment.getId());
        assertThat(findComment).isNull();
    }

    private void persistToDb() {
        em.flush();
        em.clear();
    }

    private Comment createComment(User user, Album album) {
        Comment comment = Comment.createComment(album, user, "contents");
        em.persist(comment);
        return comment;
    }

    private Album createAlbum() {
        Album album = Album.createAlbum("title", "imgUrl", null, null);
        em.persist(album);
        return album;
    }

    private User createUser() {
        User user = User.createUser("email", "name", "nickname", "pw");
        em.persist(user);
        return user;
    }
}