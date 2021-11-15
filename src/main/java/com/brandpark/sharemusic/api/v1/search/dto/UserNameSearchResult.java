package com.brandpark.sharemusic.api.v1.search.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNameSearchResult {
    private Long accountId;
    private String name;
    private String nickname;
    private String bio;
    private String profileImage;
    private LocalDateTime createdDate;
}
