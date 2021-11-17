package com.brandpark.sharemusic.api.v2.account;

import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PageHtmlResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api/v2")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final FollowRepository followRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/accounts/{targetId}/followers")
    public PageHtmlResult getFollowersPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault(size = 10) Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<FollowerInfoDto> pagingDto = accountQueryRepository.findAllFollowersByPaging(targetId, pageable);

        WebContext context = new WebContext(request, response, request.getServletContext());

        if (loginAccount != null) {

            List<Long> otherAccountFollowerIds = pagingDto.getContents().stream().map(FollowerInfoDto::getFollowerId).collect(Collectors.toList());

            Map<Long, Boolean> myAccountFollowingStateMap = followRepository.getFollowingStateByOtherAccountFollowerIds(otherAccountFollowerIds, loginAccount.getId());

            List<FollowerDtoForView> followers = new ArrayList<>();
            for (FollowerInfoDto followerInfoDto : pagingDto.getContents()) {

                FollowerDtoForView follower = new FollowerDtoForView();

                follower.setFollowerId(followerInfoDto.getFollowerId());
                follower.setName(followerInfoDto.getName());
                follower.setFollowDate(followerInfoDto.getFollowDate());
                follower.setNickname(followerInfoDto.getNickname());
                follower.setProfileImage(followerInfoDto.getProfileImage());
                follower.setFollowingState(myAccountFollowingStateMap.get(followerInfoDto.getFollowerId()));

                followers.add(follower);
            }

            context.setVariable("followers", followers);
        } else {
            context.setVariable("followers", pagingDto.getContents());
        }

        String listHtml = htmlCreator.getListHtml("partial/followers", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new PageHtmlResult(listHtml, paginationHtml);
    }

    @GetMapping("/accounts/{targetId}/followings")
    public PageHtmlResult getFollowingsPagingHtml(@LoginAccount SessionAccount loginAccount
            , @PathVariable Long targetId, @PageableDefault(size = 10) Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<FollowingInfoDto> pagingDto = accountQueryRepository.findAllFollowingsByPaging(targetId, pageable);

        WebContext context = new WebContext(request, response, request.getServletContext());

        if (loginAccount != null) {
            List<Long> followerIds = pagingDto.getContents().stream().map(FollowingInfoDto::getFollowingId).collect(Collectors.toList());
            Map<Long, Boolean> followingStateMap = followRepository.getFollowingStateByFollowingIds(followerIds, loginAccount.getId());

            List<FollowingDtoForView> followings = new ArrayList<>();
            for (FollowingInfoDto followingInfoDto : pagingDto.getContents()) {
                FollowingDtoForView following = new FollowingDtoForView();
                following.setFollowingId(followingInfoDto.getFollowingId());
                following.setName(followingInfoDto.getName());
                following.setFollowDate(followingInfoDto.getFollowingDate());
                following.setNickname(followingInfoDto.getNickname());
                following.setProfileImage(followingInfoDto.getProfileImage());
                following.setFollowingState(followingStateMap.get(followingInfoDto.getFollowingId()));

                followings.add(following);
            }

            context.setVariable("followings", followings);
        } else {
            context.setVariable("followings", pagingDto.getContents());
        }

        String listHtml = htmlCreator.getListHtml("partial/followings", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new PageHtmlResult(listHtml, paginationHtml);
    }

    @Data
    public static class FollowerDtoForView {
        private Long followerId;
        private String profileImage;
        private String nickname;
        private String name;
        private LocalDateTime followDate;
        private boolean followingState;
    }

    @Data
    public static class FollowingDtoForView {
        private Long followingId;
        private String profileImage;
        private String nickname;
        private String name;
        private LocalDateTime followDate;
        private boolean followingState;
    }
}
