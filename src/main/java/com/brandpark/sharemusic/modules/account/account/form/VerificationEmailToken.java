package com.brandpark.sharemusic.modules.account.account.form;

import lombok.Data;

@Data
public class VerificationEmailToken {

    private String email;

    private String token;
}
