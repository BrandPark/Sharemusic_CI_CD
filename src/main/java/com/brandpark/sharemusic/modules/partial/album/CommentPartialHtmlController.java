package com.brandpark.sharemusic.modules.partial.album;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.partial.PageHtmlResult;
import com.brandpark.sharemusic.modules.partial.PagingHtmlCreator;
import com.brandpark.sharemusic.modules.partial.album.form.CommentInfoForm;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
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
@RequestMapping("/partial")
@RestController
public class CommentPartialHtmlController {

    private final AlbumPartialRepository albumPartialRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/albums/{albumId}/comments")
    public PageHtmlResult getCommentListHtml(@LoginAccount SessionAccount account, @PageableDefault Pageable pageable, @PathVariable Long albumId
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<CommentInfoForm> page = albumPartialRepository.findAllComments(pageable, albumId);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("account", account);

        return  htmlCreator.getPageHtmlResult(context, page, "commentList", "partial/comments");
    }
}
