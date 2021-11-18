package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.comment.CommentService;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentApiController {

    private final AlbumQueryRepository albumQueryRepository;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final DtoValidator dtoValidator;

    @GetMapping("/albums/{albumId}/comments")
    public PagingDto<CommentDetailDto> getAllComments(@PathVariable Long albumId, @PageableDefault(size = 10) Pageable pageable) {

        return albumQueryRepository.findAllCommentDetailDtoByAlbumId(albumId, pageable);
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

        dtoValidator.validateDeleteComment(comment, account);

        commentRepository.delete(comment);
    }
}

