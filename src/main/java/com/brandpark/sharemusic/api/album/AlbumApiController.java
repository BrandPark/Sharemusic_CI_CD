package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.DtoValidator;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final DtoValidator dtoValidator;

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumSaveRequest requestDto) {

        dtoValidator.validateAlbumSaveDto(requestDto, account.getId());

        return albumService.saveAlbum(account, requestDto);
    }

    @PutMapping("/albums/{albumId}")
    public Long updateAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumUpdateRequest requestDto
            , @PathVariable("albumId") Album album) {

        dtoValidator.validateAlbumUpdateDto(requestDto, account.getId(), album.getId());

        albumService.updateAlbum(requestDto, album);

        return album.getId();
    }
}
