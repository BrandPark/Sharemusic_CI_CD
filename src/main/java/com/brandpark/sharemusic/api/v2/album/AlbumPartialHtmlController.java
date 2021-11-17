package com.brandpark.sharemusic.api.v2.album;

import com.brandpark.sharemusic.api.SearchDto;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PageHtmlResult;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class AlbumPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final PagingHtmlCreator htmlCreator;

    @GetMapping("/albums")
    public PageHtmlResult getAlbumsHtml(Pageable pageable, SearchDto searchDto) {

        PagingDto<AlbumShortDto> pagingDto = albumQueryRepository.findAllAlbumsByAccountIdList(pageable, searchDto);

        Context context = new Context();
        context.setVariable("albumList", pagingDto.getContents());
        String listHtml = htmlCreator.getListHtml("partial/albums", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new PageHtmlResult(listHtml, paginationHtml);
    }

    @GetMapping("/short-albums")
    public PageHtmlResult getShortAlbumsHtml(@PageableDefault(size=6) Pageable pageable, SearchDto searchDto) {

        PagingDto<AlbumShortDto> pagingDto = albumQueryRepository.findAllAlbumsByAccountIdList(pageable, searchDto);

        Context context = new Context();
        context.setVariable("albumPages", pagingDto);
        String listHtml = htmlCreator.getListHtml("partial/short-albums", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new PageHtmlResult(listHtml, paginationHtml);
    }
}
