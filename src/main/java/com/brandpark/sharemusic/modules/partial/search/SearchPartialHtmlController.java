package com.brandpark.sharemusic.modules.partial.search;

import com.brandpark.sharemusic.api.v1.account.query.AccountQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.SearchRequest;
import com.brandpark.sharemusic.api.v1.search.dto.UserSearchResult;
import com.brandpark.sharemusic.modules.partial.PagingHtmlCreator;
import com.brandpark.sharemusic.modules.partial.PageHtmlResult;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.modules.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.brandpark.sharemusic.modules.search.SearchType.*;

@RequiredArgsConstructor
@RequestMapping("/partial")
@RestController
public class SearchPartialHtmlController {

    private final AccountQueryRepository accountQueryRepository;
    private final AlbumQueryRepository albumQueryRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/search")
    public PageHtmlResult getSearchHtmlResult(@PageableDefault Pageable pageable
            , SearchRequest searchRequest
            , HttpServletRequest request, HttpServletResponse response) {

        SearchType type = searchRequest.getType();

        WebContext context = new WebContext(request, response, request.getServletContext());
        String listHtml = null;
        String paginationHtml = null;

        if (type == USER_NAME) {
            PagingDto<UserSearchResult> page = accountQueryRepository.findAllAccountByUserName(searchRequest.getQ(), pageable);

            context.setVariable("searchWord", searchRequest.getQ());
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-username-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == ALBUM_NAME) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByAlbumName(searchRequest.getQ(), pageable);

            context.setVariable("searchWord", searchRequest.getQ());
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-albumname-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == TRACK_NAME) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByTrackName(searchRequest.getQ(), pageable);

            context.setVariable("searchWord", searchRequest.getQ());
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-trackname-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        } else if (type == TRACK_ARTIST) {
            PagingDto<AlbumSearchResult> page = albumQueryRepository.findAllAlbumsByTrackArtist(searchRequest.getQ(), pageable);

            context.setVariable("searchWord", searchRequest.getQ());
            context.setVariable("searchResultList", page.getContents());
            context.setVariable("resultCount", page.getTotalElements());

            listHtml = htmlCreator.getListHtml("partial/search-trackartist-result", context);
            paginationHtml = htmlCreator.getPaginationHtml(page);
        }

        return new PageHtmlResult(listHtml, paginationHtml);
    }
}
