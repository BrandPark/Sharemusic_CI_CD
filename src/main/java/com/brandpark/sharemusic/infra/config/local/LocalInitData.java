package com.brandpark.sharemusic.infra.config.local;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Profile("local")
@Component
public class LocalInitData {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AlbumRepository albumRepository;
    private final CommentRepository commentRepository;
    private Account userAccount;
    private Account guestAccount;

    @PostConstruct
    public void init() {
        initAccounts();
        initAlbums();
    }

    private void initAccounts() {
        SignUpForm form = new SignUpForm();

        form.setEmail("user@email.com");
        form.setNickname("user");
        form.setPassword(passwordEncoder.encode("1q2w3e4r"));
        form.setName("박민곤");

        userAccount = modelMapper.map(form, Account.class);
        userAccount.generateEmailCheckToken();
        userAccount.assignRole(Role.USER);

        form.setEmail("guest@email.com");
        form.setNickname("guest");
        form.setPassword(passwordEncoder.encode("1q2w3e4r"));
        form.setName("박민곤");

        guestAccount = modelMapper.map(form, Account.class);
        guestAccount.generateEmailCheckToken();
        guestAccount.assignRole(Role.GUEST);

        accountRepository.saveAll(List.of(userAccount, guestAccount));
    }

    private void initAlbums() {

        for (int i = 0; i < 30; i++) {
            String title = "앨범" + i;

            Album album = Album.builder()
                    .title(title)
                    .accountId(userAccount.getId())
                    .description(title + "입니다. ============================")
                    .build();

            for (int j = 0; j < 5; j++) {
                Track track = Track.builder()
                        .album(album)
                        .name(title + ".음원" + j)
                        .artist(title + ".아티스트" + j)
                        .build();

                album.addTrack(track);
            }
            albumRepository.save(album);

            initComments(album.getId());
        }
    }
    private void initComments(Long albumId) {
        for (int j = 0; j < 2; j++) {
            Comment comment = Comment.builder()
                    .accountId(userAccount.getId())
                    .albumId(albumId)
                    .content("댓글" + j)
                    .build();

            commentRepository.save(comment);
        }
    }
}
