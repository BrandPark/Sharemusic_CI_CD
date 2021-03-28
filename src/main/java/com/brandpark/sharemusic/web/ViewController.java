package com.brandpark.sharemusic.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewController {
    @GetMapping("/")
    public String showMain() {
        return "hello";
    }
}
