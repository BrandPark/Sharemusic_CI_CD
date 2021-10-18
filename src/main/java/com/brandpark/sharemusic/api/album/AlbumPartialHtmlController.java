package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.album.query.AlbumShortDto;
import com.brandpark.sharemusic.api.album.query.CommentDetailDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class AlbumPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final TemplateEngine templateEngine;

    @GetMapping("/albums")
    public AlbumsHtmlResult getAlbumsHtml(@PageableDefault(size=6) Pageable pageable) {

        Page<AlbumShortDto> albumPages = albumQueryRepository.findAllAlbumShortDto(pageable);

        String albumListHtml = getAlbumListHtml(albumPages);

        String albumsPaginationHtml = getAlbumsPaginationButtonGroupHtml(albumPages);

        return new AlbumsHtmlResult(albumListHtml, albumsPaginationHtml);
    }

    @GetMapping("/albums/{albumId}/comments")
    public CommentHtmlResult getCommentListHtml(@LoginAccount SessionAccount account, @PageableDefault Pageable pageable, @PathVariable Long albumId
            , HttpServletRequest request, HttpServletResponse response) {

        Page<CommentDetailDto> commentPages = albumQueryRepository.findAllCommentDetailDtoByAlbumId(albumId, pageable);

        String commentHtml = getCommentsHtml(request, response, account, commentPages);

        String paginationHtml = getCommentsPaginationButtonGroupHtml(commentPages);

        return new CommentHtmlResult(commentHtml, paginationHtml);
    }

    private String getAlbumsPaginationButtonGroupHtml(Page<AlbumShortDto> albumPages) {
        Context context = new Context();
        context.setVariable("albumPages", albumPages);

        int pageCount = 0;

        if (albumPages.getTotalPages() > albumPages.getPageable().getPageSize()) {  // 10페이지 보다 많다면
            pageCount = albumPages.getPageable().getPageSize(); // 10페이지 까지만
        } else {
            pageCount = albumPages.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context.setVariable("pageArray", pageArray);

        return templateEngine.process("/albums/partial/albums-pagination", context);
    }

    private String getAlbumListHtml(Page<AlbumShortDto> albumPages) {
        Context context = new Context();
        context.setVariable("albumPages", albumPages);

        return templateEngine.process("/albums/partial/albums", context);
    }

    private String getCommentsPaginationButtonGroupHtml(Page<CommentDetailDto> commentPages) {

        Context context = new Context();
        context.setVariable("commentPages", commentPages);

        int pageCount = 0;
        if (commentPages.getTotalPages() > commentPages.getPageable().getPageSize()) {  // 10페이지 보다 많다면
            pageCount = commentPages.getPageable().getPageSize();   // 10페이지 까지만
        } else {
            pageCount = commentPages.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context.setVariable("pageArray", pageArray);

        return templateEngine.process("albums/partial/comments-pagination", context);
    }

    private String getCommentsHtml(HttpServletRequest request, HttpServletResponse response
            , SessionAccount account, Page<CommentDetailDto> commentPages) {

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("commentList", commentPages.getContent());
        context.setVariable("account", account);

        return templateEngine.process("albums/partial/comments", context);
    }

    @RequiredArgsConstructor
    @Data
    public static class CommentHtmlResult {
        final String commentsHtml;
        final String paginationHtml;
    }

    @RequiredArgsConstructor
    @Data
    public static class AlbumsHtmlResult {
        final String albumsHtml;
        final String paginationHtml;
    }
}
