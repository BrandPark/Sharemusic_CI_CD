package com.brandpark.sharemusic.infra.mail;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailMessage {

    private String title;

    private String subject;

    private String text;

    private String to;

    private String verifyLink;
}
