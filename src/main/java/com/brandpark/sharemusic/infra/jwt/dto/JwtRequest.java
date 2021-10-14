package com.brandpark.sharemusic.infra.jwt.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {

    private String username;
    private String password;
}
