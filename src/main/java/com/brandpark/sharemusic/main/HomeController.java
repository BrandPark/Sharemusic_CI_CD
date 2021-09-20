package com.brandpark.sharemusic.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String viewHome() {
        return "home";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }
}
