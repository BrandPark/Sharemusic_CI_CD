package com.brandpark.sharemusic.modules.search;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.search.form.SearchForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String searchResultView(@LoginAccount SessionAccount account, Model model
            , SearchForm form) {

        if (account != null) {
            model.addAttribute("account", account);
        }

        model.addAttribute("q", form.getQ());
        model.addAttribute("type", form.getType());
        model.addAttribute("typeName", form.getType().getName());

        return "search/search-result";
    }
}
