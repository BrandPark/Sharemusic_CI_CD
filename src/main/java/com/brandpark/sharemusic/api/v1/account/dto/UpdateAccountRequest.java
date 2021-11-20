package com.brandpark.sharemusic.api.v1.account.dto;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    private String name;
    private String nickName;
    private String bio;
    private String profileImage;
}
