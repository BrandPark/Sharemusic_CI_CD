package com.brandpark.sharemusic.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String showMain() {
        return "index";
    }
    @GetMapping("/save_album")
    public String showSaveAlbum() {
        return "save_album";
    }
}
