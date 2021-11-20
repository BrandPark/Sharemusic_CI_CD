package com.brandpark.sharemusic.modules.partial.account.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowingInfoForm {
    private Long followingId;
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followDate;
    private Boolean followingState;
}