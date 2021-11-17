package com.brandpark.sharemusic.api.v1.search;

import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v1.search.dto.UserSearchResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SearchApiController {

    private final AccountQueryRepository accountQueryRepository;

    @GetMapping("/search")
    public PagingDto<UserSearchResult> search(Pageable pageable
            , @RequestParam("q") String query, @RequestParam("type") SearchType type) {
        if (type == SearchType.USER_NAME) {
            return accountQueryRepository.findAllAccountByUserName(query, pageable);
        }
        return null;
    }
}
