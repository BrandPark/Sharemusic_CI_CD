package com.brandpark.sharemusic.api.v2.search;

import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.UserSearchResult;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.brandpark.sharemusic.modules.search.SearchType.*;

@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class SearchPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final AlbumQueryRepository albumQueryRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/search")
    public SearchHtmlResult getSearchHtmlResult(@PageableDefault Pageable pageable
            , @RequestParam("q") String query, @RequestParam("type") SearchType type
            , HttpServletRequest request, HttpServletResponse response) {

        WebContext context = new WebContext(request, response, request.getServletContext());
        String listHtml = null;
        String paginationHtml = null;

        if (type == USER_NAME) {
            PagingDto<UserSearchResult> page = accountQueryRepository.findAllAccountByUserName(query, pageable);

            context.setVariable("searchWord", query);
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-username-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == ALBUM_NAME) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByAlbumName(query, pageable);

            context.setVariable("searchWord", query);
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-albumname-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == TRACK_NAME) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByTrackName(query, pageable);

            context.setVariable("searchWord", query);
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-trackname-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == TRACK_ARTIST) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByTrackArtist(query, pageable);

            context.setVariable("searchWord", query);
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-trackartist-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        }

        return new SearchHtmlResult(listHtml, paginationHtml);
    }

    @Data
    public static class SearchHtmlResult {
        final String listHtml;
        final String paginationHtml;
    }
}
