package com.brandpark.sharemusic.modules.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateAccountDto {
    private String name;
    private String nickName;
    private String bio;
    private String profileImage;
}
