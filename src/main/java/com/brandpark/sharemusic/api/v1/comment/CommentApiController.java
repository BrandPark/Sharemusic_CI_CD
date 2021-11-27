package com.brandpark.sharemusic.api.v1.comment;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.OldApiValidator;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.comment.dto.CommentInfoResponse;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.Validator;
import com.brandpark.sharemusic.modules.comment.CommentService;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentApiController {

    private final AlbumQueryRepository albumQueryRepository;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final OldApiValidator oldApiValidator;
    private final Validator validator;

    @GetMapping("/albums/{albumId}/comments")
    public PageResult<CommentInfoResponse> findAllComments(@PathVariable Long albumId, @PageableDefault Pageable pageable) {

        validator.validateFindAllComments(albumId);

        Page<Comment> resultPage = commentRepository.findAllByAlbumId(pageable, albumId);

        List<CommentInfoResponse> resultContent = resultPage.getContent().stream()
                .map(c -> new CommentInfoResponse(
                        c.getId(),
                        c.getAccountId(),
                        c.getAlbumId(),
                        c.getContent(),
                        c.getCreatedDate(),
                        c.getModifiedDate()
                )).collect(Collectors.toList());

        return PageResultFactory.createPageResult(resultContent, pageable, resultPage.getTotalElements());
    }

    @PostMapping("/albums/{albumId}/comments")
    public Long saveComment(@LoginAccount SessionAccount account, @PathVariable Long albumId
            , @RequestParam("content") String content) {

        if (!StringUtils.hasText(content)) {
            throw new ApiException(Error.BLANK_FIELD_EXCEPTION, "댓글 내용을 입력 해주세요.");
        }

        return commentService.saveComment(albumId, account.getId(), content);
    }

    @DeleteMapping("/albums/{albumId}/comments/{commentId}")
    public void deleteComment(@LoginAccount SessionAccount account, @PathVariable("commentId") Comment comment) {

        oldApiValidator.validateDeleteComment(comment, account);

        commentRepository.delete(comment);
    }
}

