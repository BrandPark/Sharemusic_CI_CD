package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.MyUtil;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final ModelMapper modelMapper;
    private final AlbumRepository albumRepository;

    public AlbumUpdateForm entityToForm(Album album) {
        return modelMapper.map(album, AlbumUpdateForm.class);
    }

    @Transactional
    public Long saveAlbum(SessionAccount account, AlbumSaveRequest requestDto) {

        requestDto.setBio(MyUtil.toBrTag(requestDto.getBio()));
        Album album = requestDto.toEntity(account.getId());

        return albumRepository.save(album).getId();
    }
}

