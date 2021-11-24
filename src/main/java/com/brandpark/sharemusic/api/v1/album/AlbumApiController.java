package com.brandpark.sharemusic.api.v1.album;

import com.brandpark.sharemusic.api.SearchDto;
import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.v1.OldApiValidator;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final OldApiValidator oldApiValidator;
    private final AlbumQueryRepository albumQueryRepository;

    @GetMapping("/albums")
    public PageResult<AlbumShortDto> getAllAlbumShort(@PageableDefault Pageable pageable, SearchDto searchDto) {

        return albumQueryRepository.findAllAlbumsByAccountIdList(pageable, searchDto);
    }

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumSaveRequest requestDto) {

        oldApiValidator.validateAlbumSaveDto(requestDto, account.getId());

        return albumService.saveAlbum(account.getId(), requestDto);
    }

    @PutMapping("/albums/{albumId}")
    public Long updateAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumUpdateRequest requestDto
            , @PathVariable("albumId") Album album) {

        oldApiValidator.validateAlbumUpdateDto(requestDto, account.getId(), album.getId());

        albumService.updateAlbum(requestDto, album);

        return album.getId();
    }
}
