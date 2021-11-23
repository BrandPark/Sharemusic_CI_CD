package com.brandpark.sharemusic.partials.search;

import com.brandpark.sharemusic.partials.PageHtmlResult;
import com.brandpark.sharemusic.partials.PagingHtmlCreator;
import com.brandpark.sharemusic.partials.account.AccountPartialRepository;
import com.brandpark.sharemusic.partials.account.form.SearchRequestForm;
import com.brandpark.sharemusic.partials.album.AlbumPartialRepository;
import com.brandpark.sharemusic.modules.search.SearchType;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RequestMapping("/partial")
@RestController
public class SearchPartialHtmlController {

    private final AlbumPartialRepository albumPartialRepository;
    private final AccountPartialRepository accountPartialRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/search")
    public PageHtmlResult getSearchHtmlResult(@PageableDefault Pageable pageable
            , SearchRequestForm searchRequestForm
            , HttpServletRequest request, HttpServletResponse response) {

        SearchType type = searchRequestForm.getType();

        WebContext context = new WebContext(request, response, request.getServletContext());

        String viewPath = null;
        PagingDto page = null;

        switch (type) {
            case USER_NAME:
                page = accountPartialRepository.findAllAccountByUserName(pageable, searchRequestForm.getQ());
                viewPath = "partial/search-username-result"; break;
            case ALBUM_NAME:
                page = albumPartialRepository.findAllAlbumsByAlbumName(pageable, searchRequestForm.getQ());
                viewPath = "partial/search-albumname-result"; break;
            case TRACK_NAME:
                page = albumPartialRepository.findAllAlbumsByTrackName(pageable, searchRequestForm.getQ());
                viewPath = "partial/search-trackname-result"; break;
            case TRACK_ARTIST:
                page = albumPartialRepository.findAllAlbumsByTrackArtist(pageable, searchRequestForm.getQ());
                viewPath = "partial/search-trackartist-result"; break;
        }

        context.setVariable("searchWord", searchRequestForm.getQ());
        context.setVariable("searchResultList", page.getContents());
        context.setVariable("resultCount", page.getTotalElements());

        return htmlCreator.getPageHtmlResult(context, page, "searchResultList", viewPath);
    }
}
