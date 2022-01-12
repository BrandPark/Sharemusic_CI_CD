package com.brandpark.sharemusic.partials.album;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.partials.PageHtmlResult;
import com.brandpark.sharemusic.partials.PagingHtmlCreator;
import com.brandpark.sharemusic.partials.album.form.AlbumCardForm;
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
    private final FollowRepository followRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/albums")
    public PageHtmlResult homeViewAlbumsHtml(@LoginAccount SessionAccount account, Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());
        PagingDto<AlbumCardForm> page = null;

        context.setVariable("isLogin", account != null);
        if (account == null) {
            page = albumPartialRepository.findAllAlbumCardsInfo(pageable);
        } else {
            int followingCount = followRepository.countAllByFollowerId(account.getId());

            context.setVariable("followingCount", followingCount);

            page = albumPartialRepository.findAllAlbumCardsInfoAboutFollowings(pageable, account.getId());
        }

        return htmlCreator.getPageHtmlResult(context, page, "albumList", "partial/home-albums");
    }

    @GetMapping("/profile/{targetId}/albums")
    public PageHtmlResult profileViewAlbumsHtml(Pageable pageable, @PathVariable Long targetId
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<AlbumCardForm> page = albumPartialRepository.findAllProfileAlbumCardsInfo(pageable, targetId);

        WebContext context = new WebContext(request, response, request.getServletContext());
        return htmlCreator.getPageHtmlResult(context, page, "list", "partial/albums");
    }
}
