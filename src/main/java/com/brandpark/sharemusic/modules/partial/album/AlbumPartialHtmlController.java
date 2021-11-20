package com.brandpark.sharemusic.modules.partial.album;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.partial.PageHtmlResult;
import com.brandpark.sharemusic.modules.partial.PagingHtmlCreator;
import com.brandpark.sharemusic.modules.partial.album.form.AlbumCardForm;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
public class AlbumPartialHtmlController {

    private final AlbumPartialRepository albumPartialRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/albums")
    public PageHtmlResult getAlbumsHtml(@LoginAccount SessionAccount account, Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());
        PagingDto<AlbumCardForm> page = null;

        context.setVariable("isLogin", account != null);

        if (account == null) {
            page = albumPartialRepository.findAllAlbumCardsInfo(pageable);
        } else {
            context.setVariable("followingCount", albumPartialRepository.countFollowing(account.getId()));

            page = albumPartialRepository.findAllAlbumCardsInfoAboutFollowings(pageable, account.getId());
        }

        return htmlCreator.getPageHtmlResult(context, page, "albumList", "partial/home-albums");
    }

    @GetMapping("/profile/{targetId}/albums")
    public PageHtmlResult getProfileAlbumsHtml(@LoginAccount SessionAccount account, Pageable pageable
            , @PathVariable Long targetId, HttpServletRequest request, HttpServletResponse response) {

        if (account == null) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION);
        }

        PagingDto<AlbumCardForm> page = albumPartialRepository.findAllProfileAlbumCardsInfo(pageable, targetId);

        WebContext context = new WebContext(request, response, request.getServletContext());
        return htmlCreator.getPageHtmlResult(context, page, "list", "partial/albums");
    }
}
