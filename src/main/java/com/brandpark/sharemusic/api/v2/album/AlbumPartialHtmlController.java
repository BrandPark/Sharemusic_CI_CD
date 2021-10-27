package com.brandpark.sharemusic.api.v2.album;

import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v2.PagingHtmlCreator;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import lombok.Data;
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
    public AlbumsHtmlResult getAlbumsHtml(@PageableDefault(size=6) Pageable pageable) {

        PagingDto<AlbumShortDto> pagingDto = albumQueryRepository.findAllAlbumShortDto(pageable);

        Context context = new Context();
        context.setVariable("albumPages", pagingDto);
        String listHtml = htmlCreator.getListHtml("partial/albums", context);

        String paginationHtml = htmlCreator.getPaginationHtml(pagingDto);

        return new AlbumsHtmlResult(listHtml, paginationHtml);
    }

    @RequiredArgsConstructor
    @Data
    public static class AlbumsHtmlResult {
        final String albumsHtml;
        final String paginationHtml;
    }
}
