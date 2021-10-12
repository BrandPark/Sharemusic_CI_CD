package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.DtoValidator;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.album.query.AlbumShortDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public Result<Page<AlbumShortDto>> getAllAlbumShort(@PageableDefault(size = 9) Pageable pageable) {
        Page<AlbumShortDto> albumDtos = albumQueryRepository.findAllAlbumShortDto(pageable);
        return new Result<>(albumDtos, (int) albumDtos.getTotalElements());
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

    @RequiredArgsConstructor
    private static class Result<T> {
        private final T data;
        private final int count;
    }
}
