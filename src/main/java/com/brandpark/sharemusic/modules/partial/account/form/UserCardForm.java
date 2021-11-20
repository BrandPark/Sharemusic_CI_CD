package com.brandpark.sharemusic.modules.partial.account.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCardForm {
    private Long accountId;
    private String name;
    private String nickname;
    private String bio;
    private String profileImage;
    private Long followerCount;
    private Long followingCount;
    private Long albumCount;
    private LocalDateTime createdDate;
}
