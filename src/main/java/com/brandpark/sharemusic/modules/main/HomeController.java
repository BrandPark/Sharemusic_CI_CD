package com.brandpark.sharemusic.modules.main;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String viewHome(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }


        return "home";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "login";
    }
}
