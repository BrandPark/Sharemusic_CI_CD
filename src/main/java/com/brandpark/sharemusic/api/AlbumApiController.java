package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.dto.AlbumSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumRepository albumRepository;
    private final DtoValidator dtoValidator;

    @PostMapping("/albums")
    public Long createAlbum(@CurrentAccount Account account, @RequestBody AlbumSaveDto requestDto) {

        dtoValidator.validateAlbumSaveDto(requestDto);

        Album album = requestDto.toEntity(account.getId());

        return albumRepository.save(album).getId();
    }
}
