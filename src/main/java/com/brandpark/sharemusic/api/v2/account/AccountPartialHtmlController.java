package com.brandpark.sharemusic.api.v2.account;

import com.brandpark.sharemusic.api.v1.account.dto.FollowerListPagingDto;
import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.stream.IntStream;

@RequestMapping("/api/v2")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final TemplateEngine templateEngine;

    @GetMapping("/accounts/{targetId}/followers")
    public FollowersPagingHtmlResult getFollowersPagingHtml(@PathVariable Long targetId, @PageableDefault(size=10) Pageable pageable) {

        FollowerListPagingDto dto = accountQueryRepository.findAllFollowerByPaging(targetId, pageable);

        String followersHtml = getFollowersHtml(dto);

        String paginationHtml = getPaginationHtml(dto);

        return new FollowersPagingHtmlResult(followersHtml, paginationHtml);
    }

    private String getPaginationHtml(FollowerListPagingDto followersPage) {
        Context context = new Context();
        context.setVariable("followersPage", followersPage);

        int pageCount = 0;

        if (followersPage.getTotalPages() > 5) {
            pageCount = 5;
        } else {
            pageCount = followersPage.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context.setVariable("pageArray", pageArray);

        return templateEngine.process("/accounts/partial/followers-pagination", context);
    }

    private String getFollowersHtml(FollowerListPagingDto followersPage) {
        Context context = new Context();
        context.setVariable("followers", followersPage.getFollowers());

        return templateEngine.process("/accounts/partial/followers", context);
    }

    @RequiredArgsConstructor
    @Data
    public static class FollowersPagingHtmlResult {
        final String followersHtml;
        final String paginationHtml;
    }
}
