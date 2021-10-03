package com.brandpark.sharemusic.apis;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import com.brandpark.sharemusic.modules.account.validator.Validation;
import com.brandpark.sharemusic.modules.albums.domain.Album;
import com.brandpark.sharemusic.modules.albums.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.albums.dto.AlbumSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AlbumApiController {

    private final AlbumRepository albumRepository;
    private final Validation validation;

    @PostMapping("/albums")
    public Long createAlbum(@CurrentAccount Account account, @RequestBody AlbumSaveDto requestDto) {

        validation.validateAlbumSaveDto(requestDto);

        Album album = requestDto.toEntity(account.getId());

        return albumRepository.save(album).getId();
    }
}
