package com.brandpark.sharemusic.modules.search;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String searchResultView(@LoginAccount SessionAccount account, Model model
            , @RequestParam("q") String query, @RequestParam("type") SearchType type) {

        if (account != null) {
            model.addAttribute("account", account);
        }

        model.addAttribute("q", query);
        model.addAttribute("type", type);

        return "search/search-result";
    }
}
