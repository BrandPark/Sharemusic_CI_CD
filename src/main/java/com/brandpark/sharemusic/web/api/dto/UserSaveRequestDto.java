package com.brandpark.sharemusic.web.api.dto;

import com.brandpark.sharemusic.domain.Role;
import com.brandpark.sharemusic.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserSaveRequestDto {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String name;

    @NotEmpty
    private String nickname;

    @NotEmpty
    private String password;

    public User toEntity() {
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .role(Role.USER)
                .build();
    }
}

