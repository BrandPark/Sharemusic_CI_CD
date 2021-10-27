package com.brandpark.sharemusic.api.v2.account;

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
import org.thymeleaf.context.Context;

@RequestMapping("/api/v2")
@RequiredArgsConstructor
@RestController
public class AccountPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/accounts/{targetId}/followers")
    public FollowersPagingHtmlResult getFollowersPagingHtml(@PathVariable Long targetId, @PageableDefault(size=10) Pageable pageable) {

        PagingDto<FollowerInfoDto> pagingDto = accountQueryRepository.findAllFollowerByPaging(targetId, pageable);

        Context context = new Context();
        context.setVariable("followers", pagingDto.getContents());
        String listHtml = htmlCreator.getListHtml("partial/followers", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new FollowersPagingHtmlResult(listHtml, paginationHtml);
    }

    @RequiredArgsConstructor
    @Data
    public static class FollowersPagingHtmlResult {
        final String followersHtml;
        final String paginationHtml;
    }
}
