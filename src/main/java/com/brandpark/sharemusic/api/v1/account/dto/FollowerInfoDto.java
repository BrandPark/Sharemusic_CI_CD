package com.brandpark.sharemusic.api.v1.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerInfoDto {
    private Long followerId;
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followDate;
}
