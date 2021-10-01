package com.brandpark.sharemusic.modules.albums;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlbumController {

    @GetMapping("/albums/create")
    public String createAlbumForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "albums/create";
    }
}
