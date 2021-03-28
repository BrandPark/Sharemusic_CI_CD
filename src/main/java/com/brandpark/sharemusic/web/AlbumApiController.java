package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.service.albums.AlbumApiService;
import com.brandpark.sharemusic.web.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.AlbumUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AlbumApiController {
    private final AlbumApiService albumApiService;

    @PostMapping("/api/albums")
    public Long save(@RequestBody AlbumSaveRequestDto requestDto) {
        return albumApiService.save(requestDto);
    }

    @PutMapping("/api/albums/{id}")
    public Long update(@PathVariable Long id, @RequestBody AlbumUpdateRequestDto requestDto) {
        return albumApiService.update(id, requestDto);
    }

}
