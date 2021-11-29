package com.brandpark.sharemusic.api.v1.search;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.api.v1.search.dto.SearchRequest;
import com.brandpark.sharemusic.api.v1.search.query.SearchQueryRepository;
import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SearchApiController {

    private final SearchQueryRepository searchQueryRepository;

    @GetMapping("/search")
    public PageResult search(@PageableDefault Pageable pageable, SearchRequest searchRequest) {

        SearchType type = searchRequest.getType();
        String query = searchRequest.getQuery().trim();

        switch (type) {
            case USER_NAME:
                return searchQueryRepository.findAllAccountSearchResultsByNameOrNickname(pageable, query);
            case ALBUM_NAME:
                return searchQueryRepository.findAllAlbumSearchResultsByTitle(pageable, query);
        }

        throw new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION, "유효한 검색 타입이 아닙니다.");
    }
}
