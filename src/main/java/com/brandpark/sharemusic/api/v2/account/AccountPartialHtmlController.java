package com.brandpark.sharemusic.api.v2.account;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
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

@RequestMapping("/api/v2")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final DtoValidator dtoValidator;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/accounts/{targetId}/followers")
    public FollowersPagingHtmlResult getFollowersPagingHtml(@PathVariable Long targetId, @PageableDefault(size = 10) Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<FollowerInfoDto> pagingDto = accountQueryRepository.findAllFollowersByPaging(targetId, pageable);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("followers", pagingDto.getContents());
        String listHtml = htmlCreator.getListHtml("partial/followers", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new FollowersPagingHtmlResult(listHtml, paginationHtml);
    }

    @GetMapping("/accounts/{targetId}/followings")
    public FollowingsPagingHtmlResult getFollowingsPagingHtml(@PathVariable Long targetId, @PageableDefault(size = 10) Pageable pageable
            , HttpServletRequest request, HttpServletResponse response) {

        PagingDto<FollowerInfoDto> pagingDto = accountQueryRepository.findAllFollowersByPaging(targetId, pageable);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("followings", pagingDto.getContents());
        String listHtml = htmlCreator.getListHtml("partial/followings", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new FollowingsPagingHtmlResult(listHtml, paginationHtml);
    }

    @RequiredArgsConstructor
    @Data
    public static class FollowersPagingHtmlResult {
        final String followersHtml;
        final String paginationHtml;
    }

    @RequiredArgsConstructor
    @Data
    public static class FollowingsPagingHtmlResult {
        final String followingsHtml;
        final String paginationHtml;
    }
}
