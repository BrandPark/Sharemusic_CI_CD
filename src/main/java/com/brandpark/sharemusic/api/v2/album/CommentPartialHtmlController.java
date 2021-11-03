package com.brandpark.sharemusic.api.v2.album;

import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
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
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class CommentPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/albums/{albumId}/comments")
    public CommentHtmlResult getCommentListHtml(@LoginAccount SessionAccount account, @PageableDefault Pageable pageable, @PathVariable Long albumId
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<CommentDetailDto> pagingDto = albumQueryRepository.findAllCommentDetailDtoByAlbumId(albumId, pageable);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("commentList", pagingDto.getContents());
        context.setVariable("account", account);

        String listHtml = htmlCreator.getListHtml("partial/comments", context);
        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new CommentHtmlResult(listHtml, paginationHtml);
    }

    @RequiredArgsConstructor
    @Data
    public static class CommentHtmlResult {
        final String commentsHtml;
        final String paginationHtml;
    }
}
