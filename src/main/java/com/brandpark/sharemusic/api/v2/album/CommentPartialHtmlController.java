package com.brandpark.sharemusic.api.v2.album;

import com.brandpark.sharemusic.api.v1.album.dto.CommentListPagingDto;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class CommentPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final TemplateEngine templateEngine;

    @GetMapping("/albums/{albumId}/comments")
    public CommentHtmlResult getCommentListHtml(@LoginAccount SessionAccount account, @PageableDefault Pageable pageable, @PathVariable Long albumId
            , HttpServletRequest request, HttpServletResponse response) {

        CommentListPagingDto commentsDto = albumQueryRepository.findAllCommentDetailDtoByAlbumId(albumId, pageable);

        String commentHtml = getCommentListHtml(request, response, account, commentsDto);

        String paginationHtml = getPaginationHtml(commentsDto);

        return new CommentHtmlResult(commentHtml, paginationHtml);
    }

    private String getPaginationHtml(CommentListPagingDto commentPages) {

        Context context = new Context();
        context.setVariable("commentPages", commentPages);

        int pageCount = 0;
        if (commentPages.getTotalPages() > commentPages.getPageSize()) {  // 10페이지 보다 많다면
            pageCount = commentPages.getPageSize();   // 10페이지 까지만
        } else {
            pageCount = commentPages.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context.setVariable("pageArray", pageArray);

        return templateEngine.process("albums/partial/comments-pagination", context);
    }

    private String getCommentListHtml(HttpServletRequest request, HttpServletResponse response
            , SessionAccount account, CommentListPagingDto commentPages) {

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("commentList", commentPages.getComments());
        context.setVariable("account", account);

        return templateEngine.process("albums/partial/comments", context);
    }

    @RequiredArgsConstructor
    @Data
    public static class CommentHtmlResult {
        final String commentsHtml;
        final String paginationHtml;
    }
}
