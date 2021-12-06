package com.brandpark.sharemusic.partials.account.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowerInfoForm {
    private Long followerId;
    private String profileImage;
    private String nickname;
    private String name;
    private LocalDateTime followDate;
    private Boolean followingState;
}
