package com.brandpark.sharemusic.infra.config.session;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.Role;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionAccount implements Serializable {

    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    private String bio;

    private String profileImage;

    private Role role;

    private String emailCheckToken;

    public SessionAccount(Account account) {
        id = account.getId();
        name = account.getName();
        nickname = account.getNickname();
        email = account.getEmail();
        password = account.getPassword();
        bio = account.getBio();
        profileImage = account.getProfileImage();
        role = account.getRole();
        emailCheckToken = account.getEmailCheckToken();
    }
}
