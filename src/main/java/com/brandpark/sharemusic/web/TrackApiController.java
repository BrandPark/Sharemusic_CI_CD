package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.service.albums.TrackApiService;
import com.brandpark.sharemusic.web.dto.TrackSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TrackApiController {

    private final TrackApiService trackApiService;

    @PostMapping("/api/tracks")
    public void save(@RequestBody TrackSaveRequestDto requestDto) {
        trackApiService.save(requestDto);
    }
}
