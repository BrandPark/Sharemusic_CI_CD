package com.brandpark.sharemusic.api.v2.album;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumListPagingDto;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@RequestMapping("/api/v2")
@RestController
public class AlbumPartialHtmlController {

    private final AlbumQueryRepository albumQueryRepository;
    private final TemplateEngine templateEngine;

    @GetMapping("/albums")
    public AlbumsHtmlResult getAlbumsHtml(@PageableDefault(size=6) Pageable pageable) {

        AlbumListPagingDto dto = albumQueryRepository.findAllAlbumShortDto(pageable);

        String albumListHtml = getAlbumListHtml(dto);

        String albumsPaginationHtml = getPaginationHtml(dto);

        return new AlbumsHtmlResult(albumListHtml, albumsPaginationHtml);
    }

    private String getPaginationHtml(AlbumListPagingDto albumPages) {
        Context context = new Context();
        context.setVariable("albumPages", albumPages);

        int pageCount = 0;

        if (albumPages.getTotalPages() > albumPages.getPageSize()) {  // 10페이지 보다 많다면
            pageCount = albumPages.getPageSize(); // 10페이지 까지만
        } else {
            pageCount = albumPages.getTotalPages();
        }

        int[] pageArray = IntStream.range(0, pageCount).toArray();
        context.setVariable("pageArray", pageArray);

        return templateEngine.process("/albums/partial/albums-pagination", context);
    }

    private String getAlbumListHtml(AlbumListPagingDto albumPages) {
        Context context = new Context();
        context.setVariable("albumPages", albumPages);

        return templateEngine.process("/albums/partial/albums", context);
    }

    @RequiredArgsConstructor
    @Data
    public static class AlbumsHtmlResult {
        final String albumsHtml;
        final String paginationHtml;
    }
}
