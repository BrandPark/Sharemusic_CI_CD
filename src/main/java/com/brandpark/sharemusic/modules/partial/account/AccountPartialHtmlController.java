package com.brandpark.sharemusic.modules.partial.account;

import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.partial.PageHtmlResult;
import com.brandpark.sharemusic.modules.partial.PagingHtmlCreator;
import com.brandpark.sharemusic.modules.partial.account.form.FollowerInfoForm;
import com.brandpark.sharemusic.modules.partial.account.form.FollowingInfoForm;
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

@RequestMapping("/partial")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final AccountPartialRepository accountPartialRepository;
    private final FollowRepository followRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/accounts/{targetId}/followers")
    public PageHtmlResult getFollowersPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<FollowerInfoForm> page = accountPartialRepository.findAllFollowersByAccountId(pageable, targetId, loginAccount.getId());

        WebContext context = new WebContext(request, response, request.getServletContext());
        return htmlCreator.getPageHtmlResult(context, page, "followers", "partial/followers");
    }

    @GetMapping("/accounts/{targetId}/followings")
    public PageHtmlResult getFollowingsPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

//        PagingDto<FollowingInfoDto> pagingDto = accountQueryRepository.findAllFollowingsByPaging(targetId, pageable);
//
//        WebContext context = new WebContext(request, response, request.getServletContext());
//
//        if (loginAccount != null) {
//            List<Long> followerIds = pagingDto.getContents().stream().map(FollowingInfoDto::getFollowingId).collect(Collectors.toList());
//            Map<Long, Boolean> followingStateMap = followRepository.getFollowingStateByFollowingIds(followerIds, loginAccount.getId());
//
//            List<FollowingDtoForView> followings = new ArrayList<>();
//            for (FollowingInfoDto followingInfoDto : pagingDto.getContents()) {
//                FollowingDtoForView following = new FollowingDtoForView();
//                following.setFollowingId(followingInfoDto.getFollowingId());
//                following.setName(followingInfoDto.getName());
//                following.setFollowDate(followingInfoDto.getFollowingDate());
//                following.setNickname(followingInfoDto.getNickname());
//                following.setProfileImage(followingInfoDto.getProfileImage());
//                following.setFollowingState(followingStateMap.get(followingInfoDto.getFollowingId()));
//
//                followings.add(following);
//            }
//
//            context.setVariable("followings", followings);
//        } else {
//            context.setVariable("followings", pagingDto.getContents());
//        }
//
//        String listHtml = htmlCreator.getListHtml("partial/followings", context);
//
//        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);
//
//        return new PageHtmlResult(listHtml, paginationHtml);
        WebContext context = new WebContext(request, response, request.getServletContext());

        PagingDto<FollowingInfoForm> page = accountPartialRepository.findAllFollowingsByAccountId(pageable, targetId, loginAccount.getId());

        return htmlCreator.getPageHtmlResult(context, page, "followings", "partial/followings");
    }
}
