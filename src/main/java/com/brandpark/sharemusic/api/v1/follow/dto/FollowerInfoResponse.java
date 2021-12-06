package com.brandpark.sharemusic.api.v1.follow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerInfoResponse {
    private Long followerId;
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followDate;
}
