package com.brandpark.sharemusic.web.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequestDto {
    private String nickname;
    private String password;
    private String imgUrl;
    private String intro;
}
