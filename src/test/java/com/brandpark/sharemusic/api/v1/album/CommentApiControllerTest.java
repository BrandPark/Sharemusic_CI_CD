package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.comment.dto.CommentInfoResponse;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.exception.dto.ExceptionResult;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.testUtils.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class CommentApiControllerTest {

    @Autowired AlbumFactory albumFactory;
    @Autowired CommentFactory commentFactory;
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
        user = accountFactory.persistAccount("user");

        savedAlbum = albumFactory.persistAlbumWithTracks("savedAlbum", 5, user.getId());
    }

    @DisplayName("모든 댓글 페이지로 조회 - 실패(해당 앨범이 없을 경우)")
    @Test
    public void FindAllCommentsByPage_Fail_When_NotFoundAlbum() throws Exception {

        // given
        int allCommentCount = 12;
        commentFactory.persistComments("content", user.getId(), savedAlbum.getId(), allCommentCount);

        Long notExistsAlbumId = 9999L;
        boolean notExistsAlbum = !albumRepository.existsById(notExistsAlbumId);
        assertThat(notExistsAlbum).isTrue();

        String url = "/api/v1/albums/" + notExistsAlbumId + "/comments";

        int pageNum = 0;
        int pageSize = 10;

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @DisplayName("모든 댓글 페이지로 조회 - 성공(꽉찬 첫 페이지 조회)")
    @Test
    public void FindAllCommentsByPage_Success_When_FullFirstPage() throws Exception {

        // given
        int allCommentCount = 12;
        commentFactory.persistComments("content", user.getId(), savedAlbum.getId(), allCommentCount);


        int pageNum = 0;
        int pageSize = 10;

        int expectedElementsCountInPage = 10;

        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {
                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<CommentInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, allCommentCount, resultPage);

                    List<CommentInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedElementsCountInPage);

                    CommentInfoResponse resultOne = result.get(0);

                    assertCommentInfoIsNotEmpty(resultOne);
                    assertThat(resultOne.getContent()).contains("content");
                    assertThat(resultOne.getAccountId()).isEqualTo(user.getId());
                    assertThat(resultOne.getAlbumId()).isEqualTo(savedAlbum.getId());
                });
    }

    @DisplayName("모든 댓글 페이지로 조회 - 성공(덜찬 마지막 페이지 조회)")
    @Test
    public void FindAllCommentsByPage_Success_When_LessLastPage() throws Exception {

        // given
        int allCommentCount = 12;
        commentFactory.persistComments("content", user.getId(), savedAlbum.getId(), allCommentCount);


        int pageNum = 1;
        int pageSize = 10;

        int expectedElementsCountInPage = 2;

        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";

        // when
        mockMvc.perform(get(url)
                        .param("page", String.valueOf(pageNum))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(rt -> {
                    String json = rt.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    PageResult<CommentInfoResponse> resultPage = objectMapper.readValue(json, new TypeReference<>() {
                    });

                    AssertUtil.assertPageResult(pageNum, pageSize, allCommentCount, resultPage);

                    List<CommentInfoResponse> result = resultPage.getContent();
                    assertThat(result.size()).isEqualTo(expectedElementsCountInPage);

                    CommentInfoResponse resultOne = result.get(0);

                    assertCommentInfoIsNotEmpty(resultOne);
                    assertThat(resultOne.getContent()).contains("content");
                    assertThat(resultOne.getAccountId()).isEqualTo(user.getId());
                    assertThat(resultOne.getAlbumId()).isEqualTo(savedAlbum.getId());
                });
    }

    @DisplayName("댓글 생성 - 실패(로그인하지 않은 경우)")
    @Test
    public void CreateComment_Fail_When_NotAuthenticated() throws Exception {

        // given
        String commentContent = "댓글 내용";

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("content", commentContent))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 생성 - 실패(내용을 입력하지 않는 경우)")
    @Test
    public void CreateComment_Fail_When_EmptyContent() throws Exception {

        // given
        String commentContent = "";

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("content", commentContent))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException() instanceof ApiException).isTrue();

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.BLANK_FIELD_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 저장 - 성공")
    @Test
    public void CreateComment_Success() throws Exception {

        // given
        String commentContent = "댓글 내용";

        // when
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments";
        mockMvc.perform(post(url)
                        .with(csrf())
                        .param("content", commentContent))
                .andExpect(status().isOk());

        // then
        Comment comment = commentRepository.findAll().get(0);

        AssertUtil.assertEntityIsNotEmpty(comment);
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getAccountId()).isEqualTo(user.getId());
        assertThat(comment.getAlbumId()).isEqualTo(savedAlbum.getId());
    }

    @DisplayName("댓글 삭제 - 실패(로그인하지 않은 경우)")
    @Test
    public void DeleteComment_Fail_When_NotAuthenticated() throws Exception {

        // given
        Long savedCommentId = commentFactory.persistComment("content", user.getId(), savedAlbum.getId()).getId();

        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + savedCommentId;

        // when
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 실패(본인의 댓글이 아닌 경우)")
    @Test
    public void DeleteComment_Fail_When_NotAuthority() throws Exception {

        // given
        Account otherUser = accountFactory.persistAccount("otherUser");

        Long otherUserCommentId = commentFactory.persistComment("content", otherUser.getId(), savedAlbum.getId()).getId();

        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + otherUserCommentId;

        // when
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.FORBIDDEN_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 실패(앨범이 없는 경우)")
    @Test
    public void DeleteComment_Fail_When_NotFoundAlbum() throws Exception {

        // given
        Long notExistsAlbumId = 9999L;

        boolean notExistsAlbum = !albumRepository.existsById(notExistsAlbumId);
        assertThat(notExistsAlbum).isTrue();

        Long commentId = commentFactory.persistComment("content", user.getId(), savedAlbum.getId()).getId();

        String url = "/api/v1/albums/" + notExistsAlbumId + "/comments/" + commentId;

        // when
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 실패(앨범에 없는 댓글인 경우)")
    @Test
    public void DeleteComment_Fail_When_NotFoundCommentInAlbum() throws Exception {

        // given
        Long otherAlbumId = albumFactory.persistAlbumWithTracks("otherAlbum", 1, user.getId()).getId();
        Long otherAlbumCommentId = commentFactory.persistComment("content", user.getId(), otherAlbumId).getId();

        boolean notExistsInAlbum = !commentRepository.existsByIdAndAlbumId(otherAlbumCommentId, savedAlbum.getId());
        assertThat(notExistsInAlbum).isTrue();

        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + otherAlbumCommentId;

        // when
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(ApiException.class);

                    ExceptionResult exceptionResult = TestUtil.getExceptionResult(result);

                    assertThat(exceptionResult.getErrorCode()).isEqualTo(Error.ILLEGAL_ARGUMENT_EXCEPTION.getCode());
                });
    }

    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("댓글 삭제 - 성공")
    @Test
    public void DeleteComment_Success() throws Exception {

        // given
        Comment comment = commentFactory.persistComment("댓글", user.getId(), savedAlbum.getId());

        // when, then
        String url = "/api/v1/albums/" + savedAlbum.getId() + "/comments/" + comment.getId();
        mockMvc.perform(delete(url)
                        .with(csrf()))
                .andExpect(status().isOk());

        List<Comment> all = commentRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    private void assertCommentInfoIsNotEmpty(CommentInfoResponse resultOne) {
        assertThat(resultOne.getId()).isNotNull();
        assertThat(resultOne.getAccountId()).isNotNull();
        assertThat(resultOne.getAlbumId()).isNotNull();
        assertThat(resultOne.getContent()).isNotNull();
        assertThat(resultOne.getCreatedDate()).isNotNull();
        assertThat(resultOne.getModifiedDate()).isNotNull();
    }
}