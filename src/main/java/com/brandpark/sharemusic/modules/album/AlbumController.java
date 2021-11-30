package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.validator.FormValidator;
import com.brandpark.sharemusic.modules.album.form.AlbumDetailInfoForm;
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
    private final FormValidator validator;

    @GetMapping("/albums")
    public String createAlbumForm(@LoginAccount SessionAccount account, Model model) {
        model.addAttribute("account", account);
        return "albums/create";
    }

    @GetMapping("/albums/{albumId}/update")
    public String viewUpdateAlbumForm(@LoginAccount SessionAccount account, Model model, @PathVariable Long albumId) {

        validator.validateViewUpdateAlbumForm(account, albumId);

        AlbumUpdateForm form = albumService.getAlbumUpdateForm(albumId);

        model.addAttribute("account", account);
        model.addAttribute("album", form);
        model.addAttribute("tracks", form.getTracks());

        return "albums/update";
    }

    @GetMapping("/albums/{albumId}")
    public String detailAlbumView(@LoginAccount SessionAccount account, Model model, @PathVariable Long albumId) {

        validator.validateViewDetailAlbumForm(albumId);

        AlbumDetailInfoForm albumDetail = albumService.getAlbumDetailForm(albumId);

        model.addAttribute("account", account);
        model.addAttribute("albumDetailView", albumDetail);
        model.addAttribute("trackList", albumDetail.getTracks());

        return "albums/detail";
    }
}
