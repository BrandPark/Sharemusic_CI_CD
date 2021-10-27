package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.v1.DtoValidator;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final DtoValidator dtoValidator;
    private final AlbumQueryRepository albumQueryRepository;

    @GetMapping("/albums")
    public PagingDto<AlbumShortDto> getAllAlbumShort(@PageableDefault(size = 9) Pageable pageable) {
        return albumQueryRepository.findAllAlbumShortDto(pageable);
    }

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumSaveRequest requestDto) {

        dtoValidator.validateAlbumSaveDto(requestDto, account.getId());

        return albumService.saveAlbum(account.getId(), requestDto);
    }

    @PutMapping("/albums/{albumId}")
    public Long updateAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumUpdateRequest requestDto
            , @PathVariable("albumId") Album album) {

        dtoValidator.validateAlbumUpdateDto(requestDto, account.getId(), album.getId());

        albumService.updateAlbum(requestDto, album);

        return album.getId();
    }

    @Builder
    public static class ResultPage<T> {
        private T data;
        private int totalPages;
        private long totalElements;
        private int pageNumber;
        private int numberOfElements;
    }
}
