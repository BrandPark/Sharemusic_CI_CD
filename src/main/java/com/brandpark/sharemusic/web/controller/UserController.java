package com.brandpark.sharemusic.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/users/create")
    public String createForm() {

        return "users/createUserForm";
    }
}
