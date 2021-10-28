package com.brandpark.sharemusic.api.v1.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowingInfoDto {
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followingDate;
}
