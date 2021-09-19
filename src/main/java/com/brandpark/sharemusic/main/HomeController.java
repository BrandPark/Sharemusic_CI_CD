package com.brandpark.sharemusic.main;

import com.brandpark.sharemusic.account.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String viewHome(Model model){
        Account account = new Account();
        model.addAttribute("account", account);
        return "home";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }
}
