package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.infra.config.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.MyUtil;
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
    public String createAlbumForm(@LoginAccount SessionAccount account, Model model) {
        model.addAttribute("account", account);;
        return "albums/create";
    }

    @GetMapping("/albums/{albumId}")
    public String updateAlbumForm(@LoginAccount SessionAccount account, Model model, @PathVariable("albumId") Album album) {

        model.addAttribute("account", account);;

        AlbumUpdateForm form = albumService.entityToForm(album);
        form.setBio(MyUtil.toEscape(form.getBio()));

        model.addAttribute("album", form);
        model.addAttribute("tracks", form.getTracks());

        return "albums/update";
    }
}
