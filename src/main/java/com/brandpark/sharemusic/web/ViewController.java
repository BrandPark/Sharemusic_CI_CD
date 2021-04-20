package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.service.albums.AlbumApiService;
import com.brandpark.sharemusic.web.dto.albums.AlbumResponseDto;
import com.brandpark.sharemusic.web.dto.tracks.TrackResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final AlbumApiService albumApiService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("albums", albumApiService.findAllDesc());
        return "index";
    }

    @GetMapping("/albums/{id}")
    public String albumUpdate(@PathVariable Long id, Model model) {
        AlbumResponseDto responseDto = albumApiService.findById(id);
        List<TrackResponseDto> tracks = responseDto.getTracks();

        model.addAttribute("album", responseDto);
        model.addAttribute("tracks", tracks);

        return "album-update";
    }

    @GetMapping("/save_album")
    public String albumSave() {
        return "album-save";
    }

    @GetMapping("/search_track")
    public String searchTrack() {
        return "search-track";
    }

    @GetMapping("/search_youtube")
    public String searchYoutube(){ return "search-youtube"; }
}
