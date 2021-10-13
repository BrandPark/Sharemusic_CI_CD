package com.brandpark.sharemusic.infra.config.dto;

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
}
