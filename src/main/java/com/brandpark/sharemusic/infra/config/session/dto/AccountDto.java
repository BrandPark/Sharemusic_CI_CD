package com.brandpark.sharemusic.infra.config.session.dto;

import com.brandpark.sharemusic.infra.config.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountDto {

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
