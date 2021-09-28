package com.brandpark.sharemusic.account.dto;

import lombok.Data;

@Data
public class VerificationEmailToken {

    private String email;

    private String token;
}
