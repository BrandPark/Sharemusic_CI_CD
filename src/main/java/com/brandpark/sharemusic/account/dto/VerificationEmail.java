package com.brandpark.sharemusic.account.dto;

import lombok.Data;

@Data
public class VerificationEmail {

    private String email;

    private String token;
}
