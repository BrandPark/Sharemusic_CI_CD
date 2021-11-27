package com.brandpark.sharemusic.modules.comment.domain;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import com.brandpark.sharemusic.testUtils.AssertUtil;
import com.brandpark.sharemusic.testUtils.CommentFactory;
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

    @DisplayName("albumId로 Album의 모든 Comment 조회")
    @Test
    public void findAllCommentsByAlbumId() throws Exception {

        // given
        Account myAccount = accountFactory.persistAccount("myAccount");
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, myAccount.getId());

        int commentCount = 30;
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
        Account myAccount = accountFactory.persistAccount("myAccount");
        Album savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, myAccount.getId());

        int commentCount = 30;
        commentFactory.persistComments("comment", myAccount.getId(), savedAlbum.getId(), commentCount);

        assertThat(commentRepository.count()).isEqualTo(commentCount);

        // when
        System.out.println("=============시작=============");
        commentRepository.deleteByAlbumId(savedAlbum.getId());
        System.out.println("=============끝=============");

        // then
        assertThat(commentRepository.count()).isEqualTo(0);
    }
}