package com.brandpark.sharemusic.api.v1.comment;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.comment.dto.CommentInfoResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentApiController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
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
    public Long createComment(@LoginAccount SessionAccount loginAccount, @PathVariable Long albumId
            , @RequestParam("content") String content) {

        validator.validateCreateComment(albumId, content);

        return commentService.saveComment(loginAccount.getId(), albumId, content);
    }

    @DeleteMapping("/albums/{albumId}/comments/{commentId}")
    public void deleteComment(@LoginAccount SessionAccount loginAccount
            ,@PathVariable Long albumId, @PathVariable Long commentId) {

        validator.validateDeleteComment(loginAccount, albumId, commentId);

        commentRepository.deleteById(commentId);
    }
}

