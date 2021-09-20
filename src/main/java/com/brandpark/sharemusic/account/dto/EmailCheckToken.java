package com.brandpark.sharemusic.account.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailCheckToken {

    @Email
    private String email;

    private String token;
}
