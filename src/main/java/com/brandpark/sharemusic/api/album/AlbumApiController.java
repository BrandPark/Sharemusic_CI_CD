package com.brandpark.sharemusic.api.album;

import com.brandpark.sharemusic.api.DtoValidator;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.infra.config.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumService albumService;
    private final DtoValidator dtoValidator;

    @PostMapping("/albums")
    public Long createAlbum(@LoginAccount SessionAccount account, @RequestBody AlbumSaveRequest requestDto) {

        dtoValidator.validateAlbumSaveDto(requestDto);

        return albumService.saveAlbum(account, requestDto);
    }
}
