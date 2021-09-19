package com.brandpark.sharemusic.infra.mail;

import lombok.Data;

@Data
public class MailMessage {

    private String title;

    private String subject;

    private String text;

    private String to;
}
