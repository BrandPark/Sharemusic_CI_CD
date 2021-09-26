package com.brandpark.sharemusic.account.dto;

import lombok.Data;

@Data
public class VerifyEmailLink {

    private String email;

    private String token;
}
