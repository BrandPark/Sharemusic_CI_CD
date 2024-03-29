package com.brandpark.sharemusic.partials.account;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.partials.PageHtmlResult;
import com.brandpark.sharemusic.partials.PagingHtmlCreator;
import com.brandpark.sharemusic.partials.account.form.FollowerInfoForm;
import com.brandpark.sharemusic.partials.account.form.FollowingInfoForm;
import com.brandpark.sharemusic.partials.account.form.SuggestAccountForm;
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

@RequestMapping("/partial")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountPartialRepository accountPartialRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/accounts/{targetId}/followers")
    public PageHtmlResult getFollowersPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());

        PagingDto<FollowerInfoForm> page = null;

        if (loginAccount != null) {
            page = accountPartialRepository.findAllFollowersWithStateByAccountId(pageable, targetId, loginAccount.getId());

            for (FollowerInfoForm form : page.getContents()) {
                if (form.getFollowerId().equals(loginAccount.getId())) {
                    form.setFollowingState(null);
                }
            }
        } else {
            page = accountPartialRepository.findAllFollowersByAccountId(pageable, targetId);
        }

        return htmlCreator.getPageHtmlResult(context, page, "followers", "partial/followers");
    }

    @GetMapping("/accounts/{targetId}/followings")
    public PageHtmlResult getFollowingsPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());

        PagingDto<FollowingInfoForm> page = null;

        if (loginAccount != null) {
            page = accountPartialRepository.findAllFollowingsWithStateByAccountId(pageable, targetId, loginAccount.getId());

            for (FollowingInfoForm form : page.getContents()) {
                if (form.getFollowingId().equals(loginAccount.getId())) {
                    form.setFollowingState(null);
                }
            }
        } else {
            page = accountPartialRepository.findAllFollowingsByAccountId(pageable, targetId);
        }

        return htmlCreator.getPageHtmlResult(context, page, "followings", "partial/followings");
    }

    @GetMapping("/suggest-follow")
    public PageHtmlResult getSuggestFollowListPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PageableDefault Pageable pageable, HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());

        PagingDto<SuggestAccountForm> page = accountPartialRepository.findAllAccountExceptMe(pageable, loginAccount.getId());

        return htmlCreator.getPageHtmlResult(context, page, "suggests", "partial/home-suggest-follow");
    }
}
