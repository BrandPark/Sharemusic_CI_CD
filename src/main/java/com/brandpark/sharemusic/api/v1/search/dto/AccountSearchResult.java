package com.brandpark.sharemusic.api.v1.search.dto;

import com.brandpark.sharemusic.modules.account.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountSearchResult {
    private Long accountId;
    private String email;
    private String name;
    private String nickname;
    private String bio;
    private String profileImage;
    private Role role;
    private LocalDateTime createdDate;
}
