package com.brandpark.sharemusic.modules.account.dto;

import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import lombok.Data;

@Data
public class UpdateAccountDto {
    private String name;
    private String nickName;
    private String bio;
    private String profileImage;

    public UpdateAccountDto(UpdateAccountRequest request) {
        this.name = request.getName();
        this.nickName = request.getNickName();
        this.bio = request.getBio();
        this.profileImage = request.getProfileImage();
    }
}
