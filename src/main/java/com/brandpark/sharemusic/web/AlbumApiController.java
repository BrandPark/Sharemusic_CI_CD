package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.service.albums.AlbumApiService;
import com.brandpark.sharemusic.web.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.AlbumUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class AlbumApiController {
    private final AlbumApiService albumApiService;

    @PostMapping(value = "/api/albums")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody AlbumSaveRequestDto requestDto) throws IOException {
        return albumApiService.save(requestDto);
    }

    @PutMapping("/api/albums/{id}")
    public Long update(@PathVariable Long id, @RequestBody AlbumUpdateRequestDto requestDto) {
        return albumApiService.update(id, requestDto);
    }

    @DeleteMapping("/api/albums/{id}")
    public Long delete(@PathVariable Long id) {
        return albumApiService.delete(id);
    }

}
