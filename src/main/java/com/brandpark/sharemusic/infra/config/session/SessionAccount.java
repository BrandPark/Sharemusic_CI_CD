package com.brandpark.sharemusic.infra.config.session;

import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.infra.config.session.dto.AccountDto;
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

    public SessionAccount(AccountDto accountDto) {
        id = accountDto.getId();
        name = accountDto.getName();
        nickname = accountDto.getNickname();
        email = accountDto.getEmail();
        password = accountDto.getPassword();
        bio = accountDto.getBio();
        profileImage = accountDto.getProfileImage();
        role = accountDto.getRole();
        emailCheckToken = accountDto.getEmailCheckToken();
    }
}
