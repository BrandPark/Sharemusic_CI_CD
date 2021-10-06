package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.CurrentAccount;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import com.brandpark.sharemusic.modules.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/albums")
    public String createAlbumForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "albums/create";
    }

    @GetMapping("/albums/{albumId}")
    public String updateAlbumForm(@CurrentAccount Account account, Model model, @PathVariable("albumId") Album album) {

        model.addAttribute(account);

        AlbumUpdateForm form = albumService.entityToForm(album);

        model.addAttribute("album", form);
        model.addAttribute("tracks", form.getTracks());

        return "albums/update";
    }
}
