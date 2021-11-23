package com.brandpark.sharemusic.api.v1.follow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowingInfoResponse {
    private Long followingId;
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followingDate;
}
