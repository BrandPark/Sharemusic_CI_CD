package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.album.query.CommentDetailDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
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
public class AlbumPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final TemplateEngine templateEngine;

    @GetMapping("/albums/{albumId}/comments")
    public CommentHtmlResult getCommentListHtml(@LoginAccount SessionAccount account, @PageableDefault Pageable pageable, Model model, @PathVariable Long albumId
    , HttpServletRequest request, HttpServletResponse response) {

        Page<CommentDetailDto> commentPages = albumQueryRepository.findAllCommentDetailDtoByAlbumId(albumId, pageable);

        String commentHtml = getCommentsHtml(account, commentPages);

        String paginationHtml = getPaginationButtonGroupHtml(request, response, commentPages);

        return new CommentHtmlResult(commentHtml, paginationHtml);
    }

    private String getPaginationButtonGroupHtml(HttpServletRequest request, HttpServletResponse response, Page<CommentDetailDto> commentPages) {
        WebContext context2 = new WebContext(request, response, request.getServletContext());
        context2.setVariable("commentPages", commentPages);

        int pageCount = 0;
        if (commentPages.getTotalPages() > commentPages.getPageable().getPageSize()) {  // 10페이지 보다 많다면
            pageCount = commentPages.getPageable().getPageSize();
        } else {
            pageCount = commentPages.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context2.setVariable("pageArray", pageArray);

        String paginationHtml = templateEngine.process("albums/partial/pagination", context2);
        return paginationHtml;
    }

    private String getCommentsHtml(SessionAccount account, Page<CommentDetailDto> commentPages) {
        Context context = new Context();
        context.setVariable("commentList", commentPages.getContent());
        context.setVariable("account", account);

        String commentHtml = templateEngine.process("albums/partial/comments", context);
        return commentHtml;
    }

    @RequiredArgsConstructor
    @Data
    public static class CommentHtmlResult {
        final String commentsHtml;
        final String paginationHtml;
    }
}
