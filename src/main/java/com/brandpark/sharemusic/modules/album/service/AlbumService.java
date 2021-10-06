package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.modules.album.domain.Album;
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

    public AlbumUpdateForm entityToForm(Album album) {
        return modelMapper.map(album, AlbumUpdateForm.class);
    }
}

