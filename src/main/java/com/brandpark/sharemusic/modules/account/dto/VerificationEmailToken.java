package com.brandpark.sharemusic.modules.account.dto;

import lombok.Data;

@Data
public class VerificationEmailToken {

    private String email;

    private String token;
}
