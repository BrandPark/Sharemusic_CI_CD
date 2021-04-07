package com.brandpark.sharemusic.web;

import com.brandpark.sharemusic.service.albums.AlbumApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final AlbumApiService albumApiService;

    @GetMapping("/")
    public String showMain(Model model) {
        model.addAttribute("albums", albumApiService.findAllDesc());

        return "index";
    }
    @GetMapping("/save_album")
    public String showSaveAlbum() {
        return "save_album";
    }
}
