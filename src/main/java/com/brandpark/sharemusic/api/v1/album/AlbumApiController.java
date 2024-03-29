package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.api.v1.Validator;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final Validator validator;
    private final AlbumQueryRepository albumQueryRepository;

    @GetMapping("/albums")
    public PageResult<AlbumInfoResponse> getAllAlbumShort(@PageableDefault Pageable pageable) {
        return albumQueryRepository.findAllAlbumsInfo(pageable);
    }

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount loginAccount, @RequestBody @Valid CreateAlbumRequest reqDto) {

        validator.validateCreateAlbum(loginAccount, reqDto);

        return albumService.createAlbum(reqDto.toModuleDto(), loginAccount);
    }

    @PutMapping("/albums/{albumId}")
    public Long updateAlbum(@LoginAccount SessionAccount loginAccount, @RequestBody @Valid UpdateAlbumRequest reqDto
            , @PathVariable Long albumId) {

        validator.validateUpdateAlbum(loginAccount, reqDto, albumId);

        albumService.updateAlbum(reqDto.toModuleDto(), albumId);

        return albumId;
    }

    @DeleteMapping("/albums/{albumId}")
    public Long deleteAlbum(@LoginAccount SessionAccount loginAccount, @PathVariable Long albumId) {

        validator.validateDeleteAlbum(loginAccount, albumId);

        albumService.deleteAlbum(albumId);
        return albumId;
    }
}
