package com.brandpark.sharemusic.api.v1.account.dto;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.infra.config.auth.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class AccountInfoResponse {
    private Long accountId;
    private String name;
    private String nickname;
    private String email;
    private String bio;
    private String profileImage;
    private Role role;
    private Boolean emailVerified;
    private LocalDateTime createdDate;

    public AccountInfoResponse(Account account) {
        this.accountId = account.getId();
        this.name = account.getName();
        this.nickname = account.getNickname();
        this.email = account.getEmail();
        this.bio = account.getBio();
        this.profileImage = account.getProfileImage();
        this.role = account.getRole();
        this.emailVerified = account.getEmailCheckTokenGeneratedAt() != null;
        this.createdDate = account.getCreatedDate();
    }
}
