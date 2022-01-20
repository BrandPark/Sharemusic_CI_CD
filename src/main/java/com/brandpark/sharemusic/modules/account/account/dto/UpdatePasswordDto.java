package com.brandpark.sharemusic.modules.account.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdatePasswordDto {
    private String originPassword;
    private String updatePassword;
    private String confirmPassword;
}
