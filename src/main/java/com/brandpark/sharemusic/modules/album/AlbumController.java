package com.brandpark.sharemusic.modules.album;

import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.AlbumQueryRepository;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.OldValidator;
import com.brandpark.sharemusic.modules.util.MyUtil;
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
    private final AlbumQueryRepository albumQueryRepository;
    private final OldValidator validator;

    @GetMapping("/albums")
    public String createAlbumForm(@LoginAccount SessionAccount account, Model model) {
        model.addAttribute("account", account);
        return "albums/create";
    }

    @GetMapping("/albums/{albumId}/update")
    public String viewUpdateAlbumForm(@LoginAccount SessionAccount account, Model model, @PathVariable("albumId") Album album) {

        validator.validateViewUpdateAlbumForm(account.getId(), album.getAccountId());

        model.addAttribute("account", account);

        AlbumUpdateForm form = albumService.entityToForm(album);
        form.setDescription(MyUtil.toEscape(form.getDescription()));

        model.addAttribute("album", form);
        model.addAttribute("tracks", form.getTracks());

        return "albums/update";
    }

    @GetMapping("/albums/{albumId}")
    public String detailAlbumView(@LoginAccount SessionAccount account, Model model, @PathVariable Long albumId) {

        AlbumDetailDto albumDetail = albumQueryRepository.findAlbumDetailDtoById(albumId);

        model.addAttribute("account", account);
        model.addAttribute("albumDetailView", albumDetail);
        model.addAttribute("trackList", albumDetail.getTracks());

        return "albums/detail";
    }
}
