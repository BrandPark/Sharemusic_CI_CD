package com.brandpark.sharemusic.modules.comment.domain;

import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.CommentFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class CommentRepositoryTest {

    @Autowired CommentRepository commentRepository;

    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired CommentFactory commentFactory;
    Account myAccount;
    Album savedAlbum;

    @BeforeEach
    public void setUp() {
        myAccount = accountFactory.persistAccount("myAccount");
        savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, myAccount.getId());
    }

    @DisplayName("albumId로 Album의 모든 Comment 조회")
    @Test
    public void findAllCommentsByAlbumId() throws Exception {

        // given
        int commentCount = 12;
        commentFactory.persistComments("comment", myAccount.getId(), savedAlbum.getId(), commentCount);

        int pageNum = 0;
        int pageSize = 10;

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        // when
        Page<Comment> resultPage = commentRepository.findAllByAlbumId(pageRequest, savedAlbum.getId());

        // then
        AssertUtil.assertPage(pageNum, pageSize, commentCount, resultPage);

        List<Comment> resultComments = resultPage.getContent();
        assertThat(resultComments.size()).isEqualTo(pageSize);

        Comment commentOne = resultComments.get(0);

        AssertUtil.assertEntityIsNotEmpty(commentOne);
        assertThat(commentOne.getAccountId()).isEqualTo(myAccount.getId());
        assertThat(commentOne.getAlbumId()).isEqualTo(savedAlbum.getId());
        assertThat(commentOne.getContent()).contains("comment");
    }

    @DisplayName("albumId 로 해당 앨범의 댓글을 모두 삭제한다.")
    @Test
    public void DeleteAllCommentsByAlbumId() throws Exception {

        // given
        int commentCount = 3;
        commentFactory.persistComments("comment", myAccount.getId(), savedAlbum.getId(), commentCount);

        assertThat(commentRepository.count()).isEqualTo(commentCount);

        // when
        System.out.println("=============시작=============");
        commentRepository.deleteAllCommentsByAlbumId(savedAlbum.getId());
        System.out.println("=============끝=============");

        // then
        assertThat(commentRepository.count()).isEqualTo(0);
    }

    @DisplayName("albumId로 해당 앨범에 댓글이 존재하는지 확인한다. - 거짓(댓글이 없는 경우)")
    @Test
    public void ExistsByAlbumId_False_NotExists() throws Exception {

        // given
        Long notExistsCommentId = 9999L;

        boolean notExistsComment = !commentRepository.existsById(notExistsCommentId);
        assertThat(notExistsComment).isTrue();

        // when
        boolean existsInAlbum = commentRepository.existsByIdAndAlbumId(notExistsCommentId, savedAlbum.getId());

        // then
        assertThat(existsInAlbum).isFalse();
    }

    @DisplayName("albumId로 해당 앨범에 댓글이 존재하는지 확인한다. - 거짓(다른 앨범의 댓글인 경우)")
    @Test
    public void ExistsByAlbumId_False_OtherAlbumComment() throws Exception {

        // given
        Long otherAlbumId = albumFactory.persistAlbumWithTracks("otherAlbum", 1, myAccount.getId()).getId();
        Long otherAlbumCommentId = commentFactory.persistComment("otherAlbumComment", myAccount.getId(), otherAlbumId).getId();

        boolean existsComment = commentRepository.existsById(otherAlbumCommentId);
        assertThat(existsComment).isTrue();

        // when
        boolean existsInAlbum = commentRepository.existsByIdAndAlbumId(otherAlbumCommentId, savedAlbum.getId());

        // then
        assertThat(existsInAlbum).isFalse();
    }

    @DisplayName("albumId로 해당 앨범에 댓글이 존재하는지 확인한다. - 참(댓글이 있는 경우)")
    @Test
    public void ExistsByAlbumId_True_Exists() throws Exception {

        // given
        Long existsCommentId = commentFactory.persistComment("content", myAccount.getId(), savedAlbum.getId()).getId();

        boolean existsComment = commentRepository.existsById(existsCommentId);
        assertThat(existsComment).isTrue();

        // when
        boolean existsInAlbum = commentRepository.existsByIdAndAlbumId(existsCommentId, savedAlbum.getId());

        // then
        assertThat(existsInAlbum).isTrue();
    }
}