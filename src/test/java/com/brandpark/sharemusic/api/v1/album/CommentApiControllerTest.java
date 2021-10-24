package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.AlbumFactory;
import com.brandpark.sharemusic.api.v1.album.dto.CommentListPagingDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class CommentApiControllerTest {

    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired AlbumRepository albumRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    Album savedAlbum;
    Account user;
    @BeforeEach
    public void setUp() {
        user = accountFactory.createAccount("user");
        accountRepository.save(user);

        savedAlbum = albumFactory.createAlbumWithTracks(5, user.getId());
        albumRepository.save(savedAlbum);
    }

    @DisplayName("모든 댓글 페이징 조회 - 성공")
    @Test
    public void RetrieveAllCommentsByPaging_Success() throws Exception {

        // given
        Comment comment1 = albumFactory.createComment(savedAlbum.getId(), savedAlbum.getAccountId(), "댓글1");
        Comment comment2 = albumFactory.createComment(savedAlbum.getId(), savedAlbum.getAccountId(), "댓글2");
        List<Comment> saveComments = new ArrayList<>(List.of(comment1, comment2));

        commentRepository.saveAll(saveComments);

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(get(url)
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    CommentListPagingDto responseDto = objectMapper.readValue(json, CommentListPagingDto.class);

                    List<CommentDetailDto> comments = responseDto.getComments();

                    assertThat(comments.size()).isEqualTo(saveComments.size());
                    assertThat(comments.get(0).getCreateDate()).isAfter(comments.get(1).getCreateDate());
                    assertThat(comments.get(0).getContent()).isEqualTo("댓글2");
                    assertThat(comments.get(0).getWriter()).isEqualTo(user.getNickname());
                    assertThat(comments.get(0).getModifiedDate()).isEqualTo(comments.get(0).getCreateDate());
                });
    }

    @DisplayName("댓글 저장 - 실패(자격 증명이 되지 않은 상태)")
    @Test
    public void SaveComment_Fail_When_Unauthenticated() throws Exception {

        // given
        String saveContent = "댓글 내용";

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(saveContent))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 저장 - 성공")
    @Test
    public void SaveComment_Success() throws Exception {

        // given
        String saveContent = "댓글 내용";

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(saveContent))
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Long savedCommentId = objectMapper.readValue(json, Long.class);

                    assertThat(savedCommentId).isNotNull();
                });
    }

    @DisplayName("댓글 삭제 - 실패(자격 증명이 되지 않은 상태)")
    @Test
    public void RemoveComment_Fail_When_Unauthenticated() throws Exception {

        // given
        Comment comment = albumFactory.createComment(savedAlbum.getId(), user.getId(), "댓글");
        commentRepository.save(comment);

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + comment.getId();
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        Optional<Comment> commentOp = commentRepository.findById(comment.getId());
        assertThat(commentOp.isPresent()).isTrue();
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 실패(본인의 댓글이 아닌 경우)")
    @Test
    public void RemoveComment_Fail_When_NotHost() throws Exception {

        // given
        Account otherUser = accountFactory.createAccount("otherUser");
        accountRepository.save(otherUser);

        Comment otherUserComment = albumFactory.createComment(savedAlbum.getId(), otherUser.getId(), "댓글");
        commentRepository.save(otherUserComment);

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + otherUserComment.getId();
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        Optional<Comment> commentOp = commentRepository.findById(otherUserComment.getId());
        assertThat(commentOp.isPresent()).isTrue();
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 성공")
    @Test
    public void RemoveComment_Success() throws Exception {

        // given
        Comment comment = albumFactory.createComment(savedAlbum.getId(), user.getId(), "댓글");
        commentRepository.save(comment);

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + comment.getId();
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isOk());

        Optional<Comment> commentOp = commentRepository.findById(comment.getId());
        assertThat(commentOp.isEmpty()).isTrue();
    }
}